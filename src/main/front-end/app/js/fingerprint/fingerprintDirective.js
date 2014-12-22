(function(angular) {
  'use strict';

  var vitaDirectives = angular.module('vitaDirectives');

  vitaDirectives.directive('fingerprint', ['DocumentViewSender',
                                           'RelationOccurrences',
                                           'Entity',
                                           'FingerprintSynchronizer',
                                           '$routeParams',
                          function(DocumentViewSender, RelationOccurrences, Entity, FingerprintSynchronizer, $routeParams) {
    function link(scope, element, attrs) {

      var MINIMUM_SVG_HEIGHT = 40;
      var SVG_WIDTH = $(element).width();
      var SVG_HEIGHT = attrs.height || MINIMUM_SVG_HEIGHT;

      var minBarWidth = 5;

      // Defines how far the separators go above the fingerpint
      var partSeparatorTopLength = 10;
      var chapterSeparatorTopLength = 5;

      // This is the convention for margins http://bl.ocks.org/mbostock/3019563
      var margin = {top: 10, right: 5, bottom: 0, left: 5};
      var width = SVG_WIDTH - margin.left - margin.right, height = SVG_HEIGHT - margin.top - margin.bottom;

      var widthScale = d3.scale.linear()
          .domain([0, 1])
          .range([0, width]);

      var heightScale = d3.scale.linear()
          .domain([0, 1])
          .range([0, height]);

      var svgContainer = d3.select(element[0])
          .append('svg')
          .attr('width', width + margin.left + margin.right)
          .attr('height', height + margin.top + margin.bottom)
          .append('g')
          .attr("transform", "translate(" + margin.left + "," + margin.top + ")");;

      // Add a rectangle for the background
      var backgroundRect = svgContainer.append('rect')
          .attr('width', widthScale(1))
          .attr('height', heightScale(1))
          .classed('background', true)
          .attr('x', 0)
          .attr('y', 0);

      var rectGroup = svgContainer.append('g').classed('occurrences', true);
      var chapterLineGroup = svgContainer.append('g').classed('chapter-separators', true);
      var partLineGroup = svgContainer.append('g').classed('part-separators', true);

      var occurrenceSteps = calculateOccurrenceSteps();
      
      FingerprintSynchronizer();

      $(window).resize(function() {
        width = $(element).width() - margin.left - margin.right;
        svgContainer.attr('width', width + margin.left + margin.right);
        widthScale.range([0, width]);
        backgroundRect.attr('width', widthScale(1));
        calculateOccurrenceSteps();
        getRelationOccurrences();
        removeChapterSeparators();
        buildChapterSeparators(scope);
        removePartSeparators();
        buildPartSeparators(scope.parts);
      });

      scope.$watch('[entityIds,rangeBegin,rangeEnd]', function(newValues, oldValues) {
        if (!angular.equals(newValues[0], oldValues[0]) || !angular.isUndefined(newValues[0])) {
          if (angular.isUndefined(scope.entityIds) || scope.entityIds.length < 1) {
            removeFingerPrint();
            return;
          }
          getRelationOccurrences();
        }
      }, true);

      scope.$watch('parts', function(newValue, oldValue) {
        if (!angular.equals(newValue, oldValue)) {
          removeChapterSeparators();
          buildChapterSeparators(scope);
          removePartSeparators();
          buildPartSeparators(scope.parts);
        } else if (!angular.isUndefined(newValue)) {
          buildChapterSeparators(scope);
          buildPartSeparators(scope.parts);
        }
      }, true);

      function getRelationOccurrences() {
        if (angular.isUndefined(scope.entityIds)) {
          return;
        }
        RelationOccurrences.get({
          documentId: $routeParams.documentId,
          entityIds: scope.entityIds.join(','),
          steps: occurrenceSteps,
          rangeStart: scope.rangeBegin,
          rangeEnd: scope.rangeEnd
        }, function(response) {
          removeFingerPrint();
          buildFingerPrint(response.occurrences, scope);
        }, function() {
          removeFingerPrint();
        });
      }

      function buildFingerPrint(occurrences, scope) {
        occurrences = occurrences || [];
        var occurrenceCount = occurrences.length;

        occurrences.map(function(occurrence, index) {
          occurrence.index = index;
          occurrence.width = occurrence.end.progress - occurrence.start.progress;
        });


        buildOccurrenceRects();

        // mousewheel navigation
        // first call off() otherwise listeners accumulate as data changes
        $(svgContainer.node()).off();
        $(svgContainer.node()).mousewheel(onMouseWheel);

        function buildOccurrenceRects() {

          // group all the occurrence rects together
          var rectGroupEnter = rectGroup.selectAll('rect').data(occurrences).enter();

          rectGroupEnter.append('rect')
             .attr('x', function(occurrence) {
                // convert progress to actual width
                return widthScale(occurrence.start.progress);
              })
              .attr('y', heightScale(0))
              .attr('width', function(occurrence) {
                var computedWidth = widthScale(occurrence.width);
                // return at least the minimum bar width
                return Math.max(computedWidth, minBarWidth);
              })
              .attr('height', heightScale(1))
              .on('mouseover', function() {
                // Toggle selection to the hovered element
                deselectOccurrence(getSelectedOccurrence());
                selectOccurrence(d3.select(this));
              })
              .on('mouseout', function() {
                // we need to check this because the user might have scrolled and
                // selected a different occurrence
                if (isOccurrenceSelected(d3.select(this))) {
                  deselectOccurrence(d3.select(this));
                }
              })
              .on('click', function (clickedOccurrence) {
                onClickOccurrence(clickedOccurrence, scope);
              });
        }

        function onClickOccurrence(clickedOccurrence, scope) {
          DocumentViewSender.open(function() {
            RelationOccurrences.get({
              documentId: $routeParams.documentId,
              entityIds: scope.entityIds.join(','),
              rangeStart: clickedOccurrence.start.progress,
              rangeEnd: clickedOccurrence.end.progress
            }, function(response) {
              DocumentViewSender.sendOccurrences(response.occurrences);
            });
            var entitiesToSend = [];
            scope.entityIds.forEach(function(entityId) {
              Entity.get({
                documentId: $routeParams.documentId,
                entityId: entityId
              }, function(entity) {
                // Cannot simply push entity as it contains angular promises
                // which cannot be sent
                entitiesToSend.push({
                  id: entity.id,
                  displayName: entity.displayName,
                  attributes: entity.attributes,
                  rankingValue: entity.rankingValue
                });
                if (entitiesToSend.length === scope.entityIds.length) {
                  DocumentViewSender.sendEntities(entitiesToSend);
                }
              });
            });
          });
        }

        function selectOccurrence(occurrenceRect) {
          if (occurrenceRect) {
            occurrenceRect.classed('selected', true);
            // Foreground each selected rectangle
            occurrenceRect.each(function() {
              this.parentNode.appendChild(this);
            });
          }
        }

        function deselectOccurrence(occurrenceRect) {
          occurrenceRect.classed('selected', false);
        }

        function getSelectedOccurrence() {
          return svgContainer.select('.selected');
        }

        function isOccurrenceSelected(occurrenceRect) {
          return occurrenceRect.classed('selected');
        }

        function onMouseWheel(event, delta) {
          var selectedOccurence = getSelectedOccurrence();
          if (!selectedOccurence.empty()) {

            deselectOccurrence(selectedOccurence);

            // Read the index from the data
            var oldIndex = selectedOccurence.datum().index;
            // find the index of the next occurrence to select
            var newIndex = (oldIndex + delta) % occurrenceCount;

            // wrap around backwards
            if (newIndex < 0) {
              newIndex = occurrenceCount - 1;
            }

            selectOccurrence(getOccurrenceRect(newIndex));
          } else {
            // Nothing selected yet so lets just select the first occurrence
            selectOccurrence(getOccurrenceRect(0));
          }
          return false;
        }

        /**
         * Returns the occurrence rect with the given index or undefined on an invalid index.
         */
        function getOccurrenceRect(index) {
          var occurrenceRect;
          svgContainer.selectAll('.occurrences rect')
              .each(function(occurrence) {
                if (occurrence.index === index) {
                  occurrenceRect = d3.select(this);
                }
              });
          return occurrenceRect;
        }
      }

      function buildChapterSeparators(scope) {
        var chapters = angular.isUndefined(scope.parts) ? [] : getChaptersFromParts(scope.parts);

        var chapterLineGroupEnter = chapterLineGroup.selectAll('line').data(chapters).enter();

        function getChapterStartX(chapter) {
          return widthScale(chapter.range.start.progress);
        }

        function getChapterEndX(chapter) {
          return widthScale(chapter.range.end.progress);
        }

        // Build the lines that indicate the start of a chapter
        chapterLineGroupEnter.append('line')
            .attr('x1', getChapterStartX)
            .attr('x2', getChapterStartX)
            .attr('y1', function() {
              return heightScale(0) - chapterSeparatorTopLength;
            })
            .attr('y2', function() {
              return heightScale(1);
            });

        // Build the lines that indicate the end of a chapter
        chapterLineGroupEnter.append('line')
            .attr('x1', getChapterEndX)
            .attr('x2', getChapterEndX)
            .attr('y1', function() {
              return heightScale(0) - chapterSeparatorTopLength;
            }).attr('y2', function() {
              return heightScale(1);
            });
      }

      function buildPartSeparators(parts) {
        if (!parts) {
          return;
        }
        var partLineGroupEnter = partLineGroup.selectAll('line').data(parts).enter();

        function getPartStartX(part) {
          if (part.chapters.length === 0) {
            return;
          }
          return widthScale(part.chapters[0].range.start.progress) - 2.5;
        }

        partLineGroupEnter.append('line')
            .attr('x1', getPartStartX)
            .attr('x2', getPartStartX)
            .attr('y1', function() {
              return heightScale(0) - partSeparatorTopLength;
            })
            .attr('y2', function() {
              return heightScale(1);
            });
      }

      function getChaptersFromParts(parts) {
        var chapters = [];
        parts.forEach(function(part) {
          chapters = chapters.concat(part.chapters);
        });
        return chapters;
      }

      function removeFingerPrint() {
        rectGroup.selectAll('rect').remove();
      }

      function removeChapterSeparators() {
        chapterLineGroup.selectAll('line').remove();
      }

      function removePartSeparators() {
        partLineGroup.selectAll('line').remove();
      }

      function calculateOccurrenceSteps() {
        return Math.floor(width / minBarWidth);
      }
    }

    return {
      restrict: 'A',
      scope: {
        entityIds: '=',
        parts: '=',
        height: '@',
        rangeBegin: '=', // rangeStart parameter is not working with angular
        rangeEnd: '='
      },
      link: link
    };
  }]);

})(angular);
