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
      var entityMap = d3.map(), i, l, entity, relations = [];

      for (i = 0, l = entities.length; i < l; i++) {
        entity = entities[i];
        entityMap.set(entity.id, entity);
      }

      // Collect all possible relations
      for (i = 0, l = entities.length; i < l; i++) {
        entity = entities[i];

        var possibleRelations = collectPossibleRelations(entityMap, entity.entityRelations);
        setLinkAttributes(entity, possibleRelations, entityMap);

        relations = relations.concat(possibleRelations);
      }

      return {
        nodes: entityMap.values(),
        links: relations
      };
    }

    function collectPossibleRelations(displayedEntityMap, relations) {
      var possibleRelations = [];

      for (var i = 0, l = relations.length; i < l; i++) {
        var relation = relations[i];

        if (displayedEntityMap.has(relation.relatedEntity)) {
          possibleRelations.push(relation);
        }
      }

      return possibleRelations;
    }

    function setLinkAttributes(entity, relations, entityMap) {
      var i, l, relation;
      for (var i = 0, l = relations.length; i < l; i++) {
        relation = relations[i];
        relation.source = entity;
        relation.target = entityMap.get(relation.relatedEntity);
      }
    }

    return directive;
  }]);

})(angular);
