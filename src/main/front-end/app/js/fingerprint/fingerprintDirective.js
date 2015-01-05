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

      // Defines how far the separators go above the fingerprint
      var partSeparatorTopLength = 10;
      var chapterSeparatorTopLength = 5;

      // This is the convention for margins http://bl.ocks.org/mbostock/3019563
      var margin = {top: 20, right: 5, bottom: 0, left: 5};

      var width = SVG_WIDTH - margin.left - margin.right, height = SVG_HEIGHT - margin.top - margin.bottom;

      var widthScale = d3.scale.linear()
          .range([0, width]);

      var totalWidthScale = d3.scale.linear()
          .range([0, SVG_WIDTH]);

      var heightScale = d3.scale.linear()
          .range([0, height]);

      var svgContainer = d3.select(element[0])
          .append('svg')
          .attr('width', width + margin.left + margin.right)
          .attr('height', height + margin.top + margin.bottom)
          .append('g')
          .attr("transform", "translate(" + margin.left + "," + margin.top + ")");

      // Add a rectangle for the background
      var backgroundRect = svgContainer.append('rect')
          .attr('width', widthScale(1))
          .attr('height', heightScale(1))
          .classed('background', true)
          .attr('x', 0)
          .attr('y', 0);

      // This order determines the visual order in the fingerprint (last element is on top)
      var rectGroup = svgContainer.append('g').classed('occurrences', true);
      var chapterLineGroup = svgContainer.append('g').classed('chapter-separators', true);
      var partLineGroup = svgContainer.append('g').classed('part-separators', true);
      createRangeIndicators();
      var tooltip = svgContainer.append('text').classed('chapter-tooltip', true).attr('y', -margin.top);

      FingerprintSynchronizer.synchronize();

      svgContainer.on('mouseover', function() {
            tooltip.style('visibility', 'visible');
          })
          .on('mouseout', function() {
            tooltip.style('visibility', null);
          })
          .on('mousemove', function () {
            if (!scope.parts) {
              return;
            }
            var chapters = getChaptersFromParts(scope.parts);

            var xPosition = d3.mouse(this)[0];
            var progressOnMousePosition = widthScale.invert(xPosition);
            for (var i = 0, l = chapters.length; i < l; i++) {
              var chapter = chapters[i];
              if (chapter.range.end.progress > progressOnMousePosition) {
                showTooltipForChapter(chapter);
                return;
              }
            }
          });

      function showTooltipForChapter(chapter) {
        var centerOfChapter = (chapter.range.start.progress + chapter.range.end.progress) / 2;
        var tooltipPosition = widthScale(centerOfChapter);

        var tooltipBBox = tooltip.node().getBBox();
        var centerOfTooltip = tooltipBBox.width / 2;

        // should not be cut off on the left and on the right side
        tooltipPosition = Math.max(tooltipPosition, centerOfTooltip);
        tooltipPosition = Math.min(tooltipPosition, width - centerOfTooltip);

        tooltip.attr('x', tooltipPosition).text(chapter.title);
      }

      $(window).resize(function() {
        width = $(element).width() - margin.left - margin.right;
        svgContainer.attr('width', width + margin.left + margin.right);
        widthScale.range([0, width]);
        backgroundRect.attr('width', widthScale(1));

        loadOccurrencesAndDisplay();
        rebuildSeparators();
      });

      scope.$watch('entityIds', function() {
        if (angular.isUndefined(scope.entityIds) || scope.entityIds.length < 1) {
          removeFingerPrint();
        } else {
          loadOccurrencesAndDisplay();
        }
      }, true);

      scope.$watch('[rangeBegin,rangeEnd]', function() {
        var rangeStart = scope.rangeBegin || 0;
        var rangeEnd = scope.rangeEnd || 1;
        updateRangeIndicators(rangeStart, rangeEnd);
      }, true);

      scope.$watch('parts', function() {
        rebuildSeparators();
      }, true);

      function loadOccurrencesAndDisplay() {
        if (angular.isUndefined(scope.entityIds)) {
          return;
        }
        RelationOccurrences.get({
          documentId: $routeParams.documentId,
          entityIds: scope.entityIds.join(','),
          steps: calculateOccurrenceSteps()
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
          var selectedOccurrence = getSelectedOccurrence();
          if (!selectedOccurrence.empty()) {

            deselectOccurrence(selectedOccurrence);

            // Read the index from the data
            var oldIndex = selectedOccurrence.datum().index;
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

      function buildChapterSeparators(parts) {
        var chapters = getChaptersFromParts(parts);

        var chapterLineSelection = chapterLineGroup.selectAll('line').data(chapters, function(chapter) {
          return chapter.id;
        });

        chapterLineSelection.exit().remove();
        chapterLineSelection.enter().append('line')
            .attr('x1', getChapterStartX)
            .attr('x2', getChapterStartX)
            .attr('y1', function() {
              return heightScale(0) - chapterSeparatorTopLength;
            })
            .attr('y2', function() {
              return heightScale(1);
            });

        function getChapterStartX(chapter) {
          return widthScale(chapter.range.start.progress);
        }
      }

      function buildPartSeparators(parts) {
        var partLineSelection = partLineGroup.selectAll('line').data(parts, function(part) {
          return part.number;
        });

        partLineSelection.exit().remove();
        partLineSelection.enter().append('line')
            .attr('x1', getPartStartX)
            .attr('x2', getPartStartX)
            .attr('y1', function() {
              return heightScale(0) - partSeparatorTopLength;
            })
            .attr('y2', function() {
              return heightScale(1);
            });

        function getPartStartX(part) {
          if (part.chapters.length === 0) {
            return;
          }
          return widthScale(part.chapters[0].range.start.progress) - 2.5;
        }
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

      function calculateOccurrenceSteps() {
        return Math.floor(width / minBarWidth);
      }

      function rebuildSeparators() {
        var parts = scope.parts || [];
        buildChapterSeparators(parts);
        buildPartSeparators(parts);
      }

      function createRangeIndicators() {
        var rangeIndicators = svgContainer.append('g').classed('range-indicators', true);

        rangeIndicators.append('rect')
            .classed('range-indicator', true)
            .attr('id', 'range-start-indicator')
            .attr('x', -margin.left);

        rangeIndicators.append('rect')
            .classed('range-indicator', true)
            .attr('id', 'range-end-indicator')
            .attr('x', totalWidthScale(1) - margin.left);

        rangeIndicators.selectAll('rect')
            .attr('y', -margin.top)
            .attr('height', SVG_HEIGHT);
      }

      function updateRangeIndicators(rangeStart, rangeEnd) {
        svgContainer.select('#range-start-indicator').attr('width', totalWidthScale(rangeStart));

        svgContainer.select('#range-end-indicator')
            .attr('x', totalWidthScale(rangeEnd) - margin.left)
            .attr('width', totalWidthScale(1 - rangeEnd));
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
