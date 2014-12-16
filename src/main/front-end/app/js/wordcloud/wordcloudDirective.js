(function(angular) {
  'use strict';

  var vitaDirectives = angular.module('vitaDirectives');

  vitaDirectives.directive('wordcloud', ['DocumentSearch',
                                         'DocumentViewSender',
                                         '$routeParams',
                                         'Entity',
                                         'CssClass',
                                         function(DocumentSearch, DocumentViewSender, $routeParams, Entity, CssClass) {

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

      var svgContainer = d3.select(element[0])
          .append('svg')
          .attr('width', SVG_WIDTH)
          .attr('height', SVG_HEIGHT)
          .append('g')
          // >> 1 because we want to start positioning words from the center of the svg
          .attr('transform', 'translate(' + [SVG_WIDTH / 2, SVG_HEIGHT / 2] + ')');

      var cloud = d3.layout.cloud()
          .size([SVG_WIDTH, SVG_HEIGHT])
          .padding(5)
          .rotate(0)
          .text(getText)
          .fontSize(getSize)
          .on('end', draw);

      scope.$watch('items', function() {
        buildWordCloud(scope);
      }, true);

      function buildWordCloud(scope) {
        var items = scope.items || [];

        var minFrequency = getMinFrequency(items);
        var maxFrequency = getMaxFrequency(items);

        // the least frequent word gets the smallest font, most frequent the largest
        wordSizeScale = d3.scale.linear()
            .domain([minFrequency, maxFrequency])
            .range([MIN_FONTSIZE, MAX_FONTSIZE]);

        cloud.stop().words(itemsToCloudData(items), getText).start();
      }

      function draw(words) {
        var updateSelection = svgContainer.selectAll('text').data(words, getText);

        // deal with words that remain
        styleWords(updateSelection);

        // deal with words that are new
        styleWords(updateSelection.enter().append('text'));

        // deal with words to be removed
        updateSelection.exit().remove();

      }

      function styleWords(words) {
        words.style('font-size', getFontSize)
            .attr('transform', getTransform)
            .text(getText)
            .each(setClass)
            .on('click', onClick);
      }

      function onClick(word) {
        DocumentViewSender.open(function() {
          DocumentSearch.search({
            documentId: $routeParams.documentId,
            query: word.text
          }, function(response) {
            DocumentViewSender.sendOccurrences(response.occurrences);
          });
        });
      }

      /**
       * Converts an array of items from the rest-api to the cloud data of the
       * d3-cloud library
       * @param {object[]} array of word data
       */
      function itemsToCloudData(items) {
        return items.map(function(item) {
          return {
            text: item.word,
            size: wordSizeScale(item.frequency)
          };
        });
      }

      function getText(d) {
        return d.text;
      }

      function getSize(d) {
        return d.size;
      }

      function getFontSize(d) {
        return d.size + 'px';
      }

      function getTransform(d) {
        return 'translate(' + [d.x, d.y] + ')rotate(' + d.rotate + ')';
      }

      function setClass(word) {
        var wordNode = d3.select(this);
        if(!word.entityId) {
          return;
        }
        Entity.get({
          documentId: $routeParams.documentId,
          entityId: word.entityId
        }, function(entity) {
          if(entity.type === 'person') {
            wordNode.attr('class', CssClass.forRankingValue(entity.rankingValue));
          }
        });
      }
    }

    /**
     * Returns the maximum frequency from an array of word data
     * @param {object[]} array of word data
     */
    function getMaxFrequency(items) {
      return Math.max.apply(0, items.map(function(item) {
        return item.frequency;
      }));
    }

    /**
     * Returns the minimum frequency from an array of word data
     * @param {object[]} array of word data
     */
    function getMinFrequency(items) {
      return Math.min.apply(0, items.map(function(item) {
        return item.frequency;
      }));
    }

    return {
      restrict: 'A',
      scope: {
        items: '=',
        height: '@'
      },
      link: link
    };
  }]);

})(angular);
