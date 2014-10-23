(function(angular) {
  'use strict';

  var vitaDirectives = angular.module('vitaDirectives');

  vitaDirectives.directive('fingerprint', [function() {
    function link(scope, element, attrs) {

      var MINIMUM_SVG_HEIGHT = 40;
      var SVG_WIDTH = $(element).width();
      var SVG_HEIGHT = attrs.height || MINIMUM_SVG_HEIGHT;

      var width = SVG_WIDTH, height = SVG_HEIGHT;

      var widthScale = d3.scale.linear()
          .domain([0, 1])
          .range([0, width]);

      var heightScale = d3.scale.linear()
          .domain([0, 1])
          .range([0, height]);

      var svgContainer = d3.select(element[0])
          .append('svg')
          .attr('width', width)
          .attr('height', height);

      // Add a rectangle for the background
      svgContainer.append('rect')
          .attr('width', widthScale(1))
          .attr('height', heightScale(1))
          .classed('background', true)
          .attr('x', 0)
          .attr('y', 0);

      var rectGroup = svgContainer.append('g').classed('occurrences', true);
      var chapterLineGroup = svgContainer.append('g').classed('chapter-separators', true);

      scope.$watch('occurrences', function(newValue, oldValue) {
        if (!angular.equals(newValue, oldValue)) {
          removeFingerPrint();
          buildFingerPrint(scope);
        } else if (!angular.isUndefined(newValue)) {
          // can only happen during initialization
          buildFingerPrint(scope);
        }
      }, true);

      scope.$watch('parts', function(newValue, oldValue) {
        if (!angular.equals(newValue, oldValue)) {
          removeChapterSeparators();
          buildChapterSeparators(scope);
        } else if (!angular.isUndefined(newValue)) {
          buildChapterSeparators(scope);
        }
      }, true);

      function buildFingerPrint(scope) {
        // work with a copy to avoid updating the fingerprint
        var occurrences = angular.copy(scope.occurrences) || [];
        var occurrenceCount = occurrences.length;

        occurrences.map(function(occurrence, index) {
          occurrence.index = index;
          occurrence.width = occurrence.end.progress - occurrence.start.progress;
        });

        // we need to define this because otherwise small rectangles become unclickable/invisible
        var minBarWidth = 1.5;

        buildOccurrenceRects();

        // mousewheel navigation
        // first call off() otherwise listeners accumulate as data changes
        $(svgContainer.node()).off();
        $(svgContainer.node()).mousewheel(onMouseWheel);

        function buildOccurrenceRects() {

          // group all the occurrence rects together
          var rectGroupEnter = rectGroup.selectAll('rect').data(occurrences).enter();

	        rectGroupEnter.append('rect')
             .attr('x', function (occurrence) {
                // convert progress to actual width
                return widthScale(occurrence.start.progress);
              })
              .attr('y', heightScale(0))
              .attr('width', function (occurrence) {
                var computedWidth = widthScale(occurrence.width);
                // return at least the minimum bar width
                return Math.max(computedWidth, minBarWidth);
              })
              .attr('height', heightScale(1))
              .on('mouseover', function () {
                // Toggle selection to the hovered element
                deselectOccurrence(getSelectedOccurrence());
                selectOccurrence(d3.select(this));
              })
              .on('mouseout', function () {
                // we need to check this because the user might have scrolled and
                // selected a different occurrence
                if (isOccurrenceSelected(d3.select(this))) {
                  deselectOccurrence(d3.select(this));
                }
              })
              .on('click', function () {
                // TODO: Show the occurrence in the document view
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
          if (selectedOccurence.empty()) {

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
              return heightScale(0);
            })
            .attr('y2', function() {
              return heightScale(1);
            });

        // Build the lines that indicate the end of a chapter
        chapterLineGroupEnter.append('line')
            .attr('x1', getChapterEndX)
            .attr('x2', getChapterEndX)
            .attr('y1', function() {
              return heightScale(0);
            }).attr('y2', function() {
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
    }

    return {
      restrict: 'A',
      scope: {
        occurrences: '=',
        parts: '=',
        height: '@'
      },
      link: link
    };
  }]);

})(angular);
