(function(angular) {
  'use strict';

  var vitaDirectives = angular.module('vitaDirectives');

  vitaDirectives.directive('documentFingerprint', [function() {
    function link(scope, element, attrs) {


      var SVG_WIDTH = $(element).width();

      var margin = {
        top: 20,
        bottom: 20
      };

      var toolTip = d3.tip()
          .attr('class', 'document-fingerprint-tool-tip')
          .direction('w')
          .offset([0, -10])
          .html(function(d) {
            return  d.text;
          });

      var documentText;

      var visibleTextHeight = $(window).height()
          - $('#document-view-header').height() - $('#document-view-footer').height();

      d3.select(element[0]).style('right', '20px').style('top', (65 + margin.top)+"px");
      var width = SVG_WIDTH;
      var height = visibleTextHeight - margin.top - margin.bottom;

      var widthScale = d3.scale.linear()
          .range([0, width]);

      var heightScale = d3.scale.linear()
          .range([0, height - 5]);

      var svgContainer = d3.select(element[0])
          .append('svg')
          .attr('width', width)
          .attr('height', height)
          .call(toolTip);

      // Add a rectangle for the background
      var backgroundRect = svgContainer.append('rect')
          .attr('width', widthScale(1))
          .attr('height', heightScale(1))
          .classed('background', true)
          .attr('x', 0)
          .attr('y', 0);

      var rectGroup = svgContainer.append('g');

      var $documentMain = $('#document-view-main');

      scope.$watch('occurrences', function() {
        if (!angular.isUndefined(scope.occurrences)) {
          updateOccurrenceRects();
        }
      }, true);

      $(window).resize(function() {
        visibleTextHeight = $(window).height()
            - $('#document-view-header').height() - $('#document-view-footer').height();
        height = visibleTextHeight - margin.top - margin.bottom;

        widthScale = d3.scale.linear()
            .range([0, width]);

        heightScale = d3.scale.linear()
            .range([0, height - 5]);

        svgContainer.attr('height', height)
        backgroundRect.attr('height', heightScale(1));
        updateOccurrenceRects();
      });

      $documentMain.scroll(function() {
        rectGroup.selectAll('rect')
            .each(function (d, i) {
              //highlight visible occurrences
              var occurrenceSpan = getOccurrenceSpan(i);
              var rectTop = occurrenceSpan.position().top;
              var rectHeight = occurrenceSpan.height();
              var documentTop = getCurrentScrollTop();
              var isVisible = (rectTop + rectHeight > documentTop)
                  && (rectTop + rectHeight < documentTop + visibleTextHeight);
              d3.select(this).classed('highlighted', isVisible);
            });
      });

      function updateOccurrenceRects() {
        var rect = rectGroup.selectAll('rect')
            .data(scope.occurrences);
        //Update
        updateOccurrenceRect(rect);
        //Enter
        updateOccurrenceRect(rect.enter().append('rect'));
        //Exit
        rect.exit().remove();

        documentText = $('.document-view-text p').text();
        rectGroup.selectAll('rect')
            .each(setOccurrencePreviewText);
      }

      function updateOccurrenceRect(rect) {
        rect.attr('x', function() {
          return 0;
        })
        .attr('y', function(occurrence) {
          return heightScale(occurrence.start.progress);
        })
        .attr('width', function() {
          return widthScale(1);
        })
        .attr('height', function(occurrence) {
          return Math.max(heightScale(occurrence.end.progress - occurrence.start.progress), 5);
        })
        .classed('occurrence-rect', true)
        .on('click', function(occurrence, i) {
          getOccurrenceSpan(i)[0].scrollIntoView();
        })
        .on('mouseover', toolTip.show)
        .on('mouseout', toolTip.hide);
      }

      function setOccurrencePreviewText(occurrence, i) {
        var MAX_PREVIEW_LENGTH = 100;

        var lastPart = scope.parts[scope.parts.length - 1];
        var lastChapter = lastPart.chapters[lastPart.chapters.length - 1];

        // Compute how much space we have behind and in front of this occurrence
        var beforeSpace;
        if (i !== 0) {
          var prevOccurrence = scope.occurrences[i - 1];
          beforeSpace = occurrence.start.offset - prevOccurrence.end.offset;
        } else {
          beforeSpace = occurrence.start.offset;
        }

        var afterSpace;
        if (i !== scope.occurrences.length - 1) {
          var nextOccurrence = scope.occurrences[i + 1];
          afterSpace = nextOccurrence.start.offset - occurrence.end.offset;
        } else {
          afterSpace = lastChapter.range.end.offset - occurrence.end.offset;
        }

        // Take the smaller values so you don't run into the next/previous occurrence
        var beforeDiff = Math.min(beforeSpace, MAX_PREVIEW_LENGTH);
        var afterDiff = Math.min(afterSpace, MAX_PREVIEW_LENGTH);

        // Also check so we don't run into beginning/end of document
        var beforeStartOffset =  Math.max(0, occurrence.start.offset - beforeDiff);
        var afterEndOffset = Math.min(lastChapter.range.end.offset, occurrence.end.offset + afterDiff);

        var beforeText = documentText.slice(beforeStartOffset, occurrence.start.offset);
        var afterText =  documentText.slice(occurrence.end.offset, afterEndOffset);

        beforeText = beforeDiff === MAX_PREVIEW_LENGTH ? '...' + beforeText : beforeText;
        afterText = afterDiff === MAX_PREVIEW_LENGTH ? afterText + '...' : afterText;
        occurrence.text = beforeText + '<span class="occurrence">'
            + getOccurrenceSpan(i).html() + '</span>' + afterText;
      }

      function getCurrentScrollTop() {
        return $documentMain.scrollTop();
      }

      function getOccurrenceSpan(index) {
        return $('#occurrence-' + index);
      }
    }

    return {
      restrict: 'A',
      scope: {
        occurrences: '=',
        parts: '='
      },
      link: link
    };
  }]);

})(angular);
