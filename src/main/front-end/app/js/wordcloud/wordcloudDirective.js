(function(angular) {
  'use strict';

  var vitaDirectives = angular.module('vitaDirectives');

  vitaDirectives.directive('wordcloud', [function() {

    function link(scope, element, attrs) {

      var MINIMUM_SVG_WIDTH = 500;
      var MINIMUM_SVG_HEIGHT = 500;

      var SVG_WIDTH = Math.max($(element).width(), MINIMUM_SVG_WIDTH);
      var SVG_HEIGHT = attrs.height || MINIMUM_SVG_HEIGHT;

      // The minimum readable font size for a word in the wordcloud
      var MIN_FONTSIZE = 10;

      // The maximum font size for a word in the wordcloud
      var MAX_FONTSIZE = 60;

      // maps from the frequency of a word to its font size
      var wordSizeScale;

      // TODO: Coloring?
      var fill = d3.scale.category20();

      // create the central svg element all other elements live in
      var svg = d3.select(element[0])
          .append('svg')
          .attr('width', SVG_WIDTH)
          .attr('height', SVG_HEIGHT)
          .append('g')
          // >> 1 because we want to start positioning words from the center of the svg
          .attr('transform', 'translate(' + [SVG_WIDTH >> 1, SVG_HEIGHT >> 1] + ')');

      // create the cloud layout
      var cloud = d3.layout.cloud()
          .size([SVG_WIDTH, SVG_HEIGHT])
          .padding(5)
          .rotate(0)
          .text(getText)
          .fontSize(getSize)
          .on("end", draw);

      // wait for our data to load
      scope.$watch('items', function(newValue, oldValue) {
        buildWordCloud(scope, element, attrs);
      }, true);

      function buildWordCloud(scope, element, attrs) {
        var items = scope.items || [];

        var minFrequency = getMinFrequency(items);
        var maxFrequency = getMaxFrequency(items);

        // the least frequent word gets the smallest font, most frequent the
        // largest
        wordSizeScale = d3.scale.linear()
            .domain([minFrequency, maxFrequency])
            .range([MIN_FONTSIZE, MAX_FONTSIZE]);

        cloud.stop().words(itemsToCloudData(items), getText).start();
      }

      // Actually draw/move/remove words in the wordcloud as we now know their position
      function draw(words) {
        var updateSelection = svg.selectAll("text").data(words, getText);

        // deal with words that remain
        styleWords(updateSelection);

        // deal with words that are new
        styleWords(updateSelection.enter().append("text"));

        // deal with words to be removed
        updateSelection.exit().remove();

      }

      function styleWords(words) {
        words
            .style("font-size", getFontSize)
            .style("fill", getFill)
            .attr("transform", getTransform)
            .text(getText);
      }

      // Converts an array of items from the rest-api to the cloud data of the
      // d3-cloud library
      function itemsToCloudData(items) {
        return items.map(function(item) {
          return {
            text: item.word,
            size: wordSizeScale(item.frequency),
          };
        });
      }

      // Various functions to access certain attributes of data

      function getText(d) {
        return d.text;
      }

      function getSize(d) {
        return d.size;
      }

      function getFontSize(d) {
        return d.size + "px";
      }

      function getTransform(d) {
        return "translate(" + [d.x, d.y] + ")rotate(" + d.rotate + ")";
      }

      function getFill(d, i) {
        return fill(i);
      }
    }

    // Returns the maximum frequency from an array of items
    function getMaxFrequency(items) {
      return Math.max.apply(0, items.map(function(item) {
        return item.frequency;
      }));
    }

    //Returns the minimum frequency from an array of items
    function getMinFrequency(items) {
      return Math.min.apply(0, items.map(function(item) {
        return item.frequency;
      }));
    }

    return {
      restrict: 'A',
      scope: {
        items: '=',
        height: '@',
      },
      link: link,
    };
  }]);

})(angular);