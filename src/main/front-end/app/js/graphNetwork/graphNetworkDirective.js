(function(angular) {
  'use strict';

  var vitaDirectives = angular.module('vitaDirectives');

  vitaDirectives.directive('graphNetwork', [function() {

    var directive = {
      replace: false,
      restrict: 'EA',
      scope: {
        entities: '=',
        width: '@',
        height: '@'
      },
      link: function(scope, element) {
        buildGraph(scope, element);
      }
    };

    var force, nodes, links;

    function buildGraph(scope, element) {
      var container = d3.select(element[0]);
      var width = scope.width || 800;
      var height = scope.height || 400;

      var graph = container.append("svg")
          .classed("graph-network", true)
           .attr("width", width)
           .attr("height", height)
           .append('g'); // an extra group for zooming

      var graphData = parseEntitiesToGraphData(scope.entities);

      var force = d3.layout.force()
          .nodes(graphData.nodes)
          .links(graphData.links)
          .size([width, height])
          .linkDistance(100)
          .on('tick', setNewPositions)
          .start();

      links = graph.selectAll('.link')
          .data(graphData.links)
          .enter().append('line')
          .classed('link', true);

      nodes = graph.selectAll('.node')
          .data(graphData.nodes)
          .enter().append('circle')
          .classed('node', true)
          .attr('r', 20)
          .call(force.drag);
    }

    function setNewPositions() {
      nodes.attr('cx', function(d) {
        return d.x;
      }).attr('cy', function(d) {
        return d.y;
      });

      links.attr('x1', function(d) {
        return d.source.x;
      }).attr('y1', function(d) {
        return d.source.y;
      }).attr('x2', function(d) {
        return d.target.x;
      }).attr('y2', function(d) {
        return d.target.y;
      });
    }

    function parseEntitiesToGraphData(entities) {
      // TODO replace with parsing logic
      var nodes = [{
        id: '1'
      }, {
        id: '2'
      }];
      var links = [{
        source: nodes[0],
        target: nodes[1]
      }];

      return {
        nodes: nodes,
        links: links
      };
    }

    return directive;
  }]);

})(angular);
