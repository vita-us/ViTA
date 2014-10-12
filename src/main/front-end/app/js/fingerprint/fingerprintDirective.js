(function(angular) {
  'use strict';

  var vitaDirectives = angular.module('vitaDirectives');

  vitaDirectives.directive('fingerprint', [function() {
    function link(scope, element, attrs) {

      var MINIMUM_SVG_HEIGHT = 40;
      var SVG_WIDTH = $(element).width();
      var SVG_HEIGHT = attrs.height || MINIMUM_SVG_HEIGHT;

      var margin = {
        top: 5,
        right: 5,
        bottom: 5,
        left: 5
      };

      // compute width and height considering margins
      var width = SVG_WIDTH - margin.left - margin.right, height = SVG_HEIGHT - margin.top
              - margin.bottom;

      // Scale for the width of rectangles
      var widthScale = d3.scale.linear().domain([0, 1]).range([0, width]);
      // Scale fot the height of rectangles
      var heightScale = d3.scale.linear().domain([0, 1]).range([0, height]);

      // create the central svg element all other elements live in
      var svg = d3.select(element[0]).append('svg').attr('width',
              width + margin.left + margin.right).attr('height',
              height + margin.top + margin.bottom).append('g').attr('transform',
              'translate(' + margin.left + ',' + margin.top + ')');

      // make background rectangle all the occurrence rectangles are inside
      var backgroundRect = svg.append('rect').attr('width', widthScale(1)).attr('height',
              heightScale(1)).classed('background', true).attr('x', 0).attr('y', 0);

      // Define groups for occurrence rects and chapter lines
      var rectGroup = svg.append('g').classed('occurrences', true);
      var chapterLineGroup = svg.append('g').classed('chapter-separators', true);

      // watch for our occurrences to load/change
      scope.$watch('occurrences', function(newValue, oldValue) {
        if (!angular.equals(newValue, oldValue)) {
          removeFingerPrint();
          buildFingerPrint(scope, element, attrs);
        } else if (!angular.isUndefined(newValue)) {
          // can only happen during initialization
          buildFingerPrint(scope, element, attrs);
        }
      }, true);

      // watch for our parts to load/change
      scope.$watch('parts', function(newValue, oldValue) {
        if (!angular.equals(newValue, oldValue)) {
          removeChapterSeparators();
          buildChapterSeparators(scope, element, attrs);
        } else if (!angular.isUndefined(newValue)) {
          buildChapterSeparators(scope, element, attrs);
        }
      }, true);

      /**
       * Builds the fingerprint including the occurrence rects
       */
      function buildFingerPrint(scope, element, attrs) {
        // work with a copy to avoid updating the fingerprint
        var occurrences = angular.copy(scope.occurrences) || [];
        var occurrenceCount = occurrences.length;

        occurrences.map(function(occurrence, index) {
          occurrence.index = index;
          occurrence.width = occurrence.end.progress - occurrence.start.progress;
        });

        // we need to define this because otherwise small rectangles become
        // unclickable/invisible
        var minBarWidth = 1.5;

        // Helper function to append a node to the end of its parent
        // as svg elements are drawn on top of each other in order of dom
        // structure
        d3.selection.prototype.moveToFront = function() {
          return this.each(function() {
            this.parentNode.appendChild(this);
          });
        };

        buildOccurrenceRects();

        // mousewheel navigation
        // svg[0] because we need to convert from d3 selector to jquery
        // first call off() otherwise listeners accumulate as data changes
        $(svg[0]).off();
        $(svg[0]).mousewheel(onMouseWheel);

        function buildOccurrenceRects() {

          // group all the occurrence rects together

          var rectGroupEnter = rectGroup.selectAll('rect').data(occurrences).enter();

          rectGroupEnter.append('rect').attr('x', function(occurrence, i) {
            // convert progress to actual width
            return widthScale(occurrence.start.progress);
          }).attr('y', heightScale(0)).attr('width', function(occurrence) {

            var computedWidth = widthScale(occurrence.width);
            // return at least the minimum bar width
            return Math.max(computedWidth, minBarWidth);

          }).attr('height', heightScale(1)).on('mouseover', function() {

            // deselect the old occurrence if possible
            if (!getSelectedOccurrence().empty()) {
              var oldSelected = getSelectedOccurrence(svg);
              deselectOccurrence(oldSelected);
            }

            selectOccurrence(d3.select(this));

          }).on('mouseout', function() {

            // we need to check this because the user might have scrolled and
            // selected a different occurrence
            if (isSelected(d3.select(this))) {
              deselectOccurrence(d3.select(this));
            }

          }).on('click', function() {
            // TODO: Show the occurrence in the document view
          });
        }

        // Select the occurrence of the given rect
        function selectOccurrence(occurrenceRect) {
          occurrenceRect.classed('selected', true);
          occurrenceRect.moveToFront();
        }

        // Deselect the occurrence of the given rect
        function deselectOccurrence(occurrenceRect) {
          // move it to the front so its visible
          occurrenceRect.classed('selected', false);
        }

        // Get the rect of the currently selected occurrence
        function getSelectedOccurrence() {
          return svg.select('.selected');
        }

        // Return true if the given rect is selected, false otherwise
        function isSelected(occurrenceRect) {
          return occurrenceRect.classed('selected');
        }

        // Called when a mousewheel event happens inside the svg
        function onMouseWheel(event, delta) {

          if (!getSelectedOccurrence().empty()) {

            // deselect the old occurrence
            var oldSelected = getSelectedOccurrence(svg);
            deselectOccurrence(oldSelected);

            // Read the index from the data
            var oldIndex = oldSelected.datum().index;
            // find the index of the next occurrence to select
            var newIndex = (oldIndex + delta) % occurrenceCount;

            // wrap around backwards
            if (newIndex < 0) {
              newIndex = occurrenceCount - 1;
            }

            // select the next occurrence rectangle
            selectOccurrence(getOccurrenceRect(newIndex));

          } else {

            // Nothing selected yet so lets just select the first
            // occurrence
            selectOccurrence(getOccurrenceRect(0));

          }
          return false;
        }

        /**
         * Return the occurrence rect with the given index, null if no such rect
         * exists
         */
        function getOccurrenceRect(index) {
          var occurrenceRect = null;
          svg.selectAll('.occurrences rect').each(function(occurrence, i) {
            if (occurrence.index === index) {
              occurrenceRect = d3.select(this);
            }
          });
          return occurrenceRect;
        }

      }

      /**
       * Builds the chapter separators
       */
      function buildChapterSeparators(scope, element, attrs) {
        var chapters = angular.isUndefined(scope.parts) ? [] : getChaptersFromParts(scope.parts);

        var chapterLineGroupEnter = chapterLineGroup.selectAll('line').data(chapters).enter();

        // Define these function here because we need them several times
        function getChapterStartX(chapter) {
          return widthScale(chapter.range.start.progress);
        }

        function getChapterEndX(chapter) {
          return widthScale(chapter.range.end.progress);
        }

        // Build the lines that indicate the start of a chapter
        chapterLineGroupEnter.append('line').attr('x1', getChapterStartX).attr('x2',
                getChapterStartX).attr('y1', function() {
          return heightScale(0);
        }).attr('y2', function() {
          return heightScale(1);
        });

        // Build the lines that indicate the end of a chapter
        chapterLineGroupEnter.append('line').attr('x1', getChapterEndX).attr('x2', getChapterEndX)
                .attr('y1', function() {
                  return heightScale(0);
                }).attr('y2', function() {
                  return heightScale(1);
                });

      }

      /**
       * Returns an array of chapters from the given parts
       */
      function getChaptersFromParts(parts) {
        var chapters = [];
        parts.forEach(function(part) {
          chapters = chapters.concat(part.chapters);
        });
        return chapters;
      }

      /**
       * Removes the fingerprint including the occurence rects
       */
      function removeFingerPrint() {
        rectGroup.selectAll('rect').remove();
      }

      /**
       * Removes the chapter separators
       */
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
      link: link,
    };
  }]);

})(angular);
