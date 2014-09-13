(function(angular) {
  'use strict';

  var vitaDirectives = angular.module('vitaDirectives');

  vitaDirectives.directive('fingerprint', [function() {
    function link(scope, element, attrs) {

      // wait for our data to load
      scope.$watch('data', function(data) {
        if (data !== undefined) {
          buildFingerPrint(scope, element, attrs);
        }
      });

    }

    function buildFingerPrint(scope, element, attrs) {
      var occurrences = scope.data.occurrences;
      var occurrenceCount = occurrences.length;

      occurrences.map(function(occurrence, index) {
        occurrence.index = index;
        occurrence.width = occurrence.end.progress - occurrence.start.progress;
      });

      // TODO: don't hardcode the size of the svg
      var SVG_WIDTH = 800;
      var SVG_HEIGHT = 40;

      var margin = {
        top: 5,
        right: 5,
        bottom: 5,
        left: 5
      };

      // we need to define this because otherwise small rectangles become
      // unclickable/invisible
      var minBarWidth = 1.5;

      // define how much bigger occurrence rects get when selected
      var extraBarWidthSelected = 3;
      var extraBarHeightSelected = 3;

      // Helper function to append a node to the end of its parent
      // as svg elements are drawn on top of each other in order of dom
      // structure
      d3.selection.prototype.moveToFront = function() {
        return this.each(function() {
          this.parentNode.appendChild(this);
        });
      };

      // compute width and height considering margins
      var width = SVG_WIDTH - margin.left - margin.right, height = SVG_HEIGHT - margin.top
              - margin.bottom;

      // Scale for the width of rectangles
      var widthScale = d3.scale.linear().domain([0, 1]).range([0, width]);

      var heightScale = d3.scale.linear().domain([0, 1]).range([0, height]);

      // create the central svg element all other elements live in
      var svg = d3.select(element[0]).append('svg').attr('width',
              width + margin.left + margin.right).attr('height',
              height + margin.top + margin.bottom).append('g').attr('transform',
              'translate(' + margin.left + ',' + margin.top + ')');

      // make background rectangle all the occurrence rectangles are inside
      var backgroundRect = svg.append('rect').attr('width', widthScale(1)).attr('height',
              heightScale(1)).classed('background', true).attr('x', 0).attr('y', 0);

      buildOccurrenceRects();

      // mousewheel navigation
      // svg[0] because we need to convert from d3 selector to jquery
      $(svg[0]).mousewheel(onMouseWheel);

      function buildOccurrenceRects() {

        // group all the occurrence rects together
        var rectGroup = svg.append('g').classed('occurrences', true);
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
        enlargeOccurrenceRect(occurrenceRect, extraBarHeightSelected, extraBarWidthSelected);

        occurrenceRect.classed('selected', true);
        occurrenceRect.moveToFront();
      }

      // Deselect the occurrence of the given rect
      function deselectOccurrence(occurrenceRect) {
        enlargeOccurrenceRect(occurrenceRect, -1 * extraBarHeightSelected, -1
                * extraBarWidthSelected);
        // move it to the front so its visible
        occurrenceRect.classed('selected', false);
      }

      // Get the rect of the currently selected occurrence
      function getSelectedOccurrence() {
        return svg.select('.selected');
      }

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

      // Return the occurrence rect with the given index
      // null if no such rect exists
      function getOccurrenceRect(index) {
        var occurrenceRect;
        svg.selectAll('.occurrences rect').each(function(occurrence, i) {
          if (occurrence.index === index) {
            occurrenceRect = d3.select(this);
          }
        });
        return occurrenceRect;
      }

      // Enlargens an occurrence rect by the given values while also displacing
      // it
      // so it appears to have the same center
      // negative values make the rect smaller
      function enlargeOccurrenceRect(occurrenceRect, extraHeight, extraWidth) {
        var rectHeight = parseFloat(occurrenceRect.attr('height'));
        var rectWidth = parseFloat(occurrenceRect.attr('width'));

        var rectX = occurrenceRect.attr('x');
        var rectY = occurrenceRect.attr('y');

        occurrenceRect.attr('height', rectHeight + extraHeight);
        occurrenceRect.attr('width', rectWidth + extraWidth);

        occurrenceRect.attr('x', rectX - extraWidth / 2);
        occurrenceRect.attr('y', rectY - extraHeight / 2);
      }

    }

    return {
      restrict: 'EA',
      scope: {
        data: '=data'
      },
      link: link,
    };
  }]);

})(angular);
