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
        buildGraph(element, scope.entities, scope.width, scope.height);

        scope.$watch('entities', function(newValue, oldValue) {
          if (!angular.equals(newValue, oldValue)) {
            updateGraph(scope.entities);
          }
        }, true);
      }
    };

    var graph, force, nodes, links;

    function buildGraph(element, entities, width, height) {
      var container = d3.select(element[0]);
      var width = width || 800;
      var height = height || 400;

      graph = container.append("svg")
          .classed("graph-network", true)
          .attr("width", width)
          .attr("height", height)
          .append('g'); // an extra group for zooming

      var graphData = parseEntitiesToGraphData(entities);

      force = d3.layout.force()
          .nodes(graphData.nodes)
          .links(graphData.links)
          .size([width, height])
          .linkDistance(100)
          .on('tick', setNewPositions);

      redrawElements(graphData);

      force.start();
    }

    function redrawElements(graphData) {
      /* Remove all elements because they are redrawn. This is the only solution currently
       * because it isn't guaranteed, that the controller is passing the same objects for the same
       * displayed entities. For example one entity might disappear, but this directive receives 
       * completely new objects - even for unchanged entities. */
      graph.selectAll('*').remove();

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
      var entityMap = d3.map(), i, l, entity, links = [];

      for (i = 0, l = entities.length; i < l; i++) {
        entity = entities[i];
        entityMap.set(entity.id, entity);
      }

      var nodeMap = createNodeMap(entities);

      // Collect all possible relations and create links
      for (i = 0, l = entities.length; i < l; i++) {
        entity = entities[i];

        var possibleRelations = collectPossibleRelations(entityMap, entity.entityRelations);
        var createdLinks = createLinks(entity, possibleRelations, nodeMap);

        links = links.concat(createdLinks);
      }

      return {
        nodes: nodeMap.values(),
        links: links
      };
    }

    function createNodeMap(entities) {
      var nodeMap = d3.map();

      for (var i = 0, l = entities.length; i < l; i++) {
        var entity = entities[i];

        // Create a shallow copy. We need this, because otherwise d3 would modify the original data
        nodeMap.set(entity.id, {
          id: entity.id,
          displayName: entity.displayName,
          type: entity.type,
          rankingValue: entity.rankingValue
        });
      }

      return nodeMap;
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

    function createLinks(entity, relations, nodeMap) {
      var links = [];

      for (var i = 0, l = relations.length; i < l; i++) {
        var relation = relations[i];

        var link = {
          // d3 graph attributes
          source: nodeMap.get(entity.id),
          target: nodeMap.get(relation.relatedEntity),
          // copy other useful attributes
          relatedEntity: nodeMap.get(relation.relatedEntity),
          weight: relation.weight
        }

        links.push(link);
      }

      return links;
    }

    function updateGraph(entities) {
      var graphData = parseEntitiesToGraphData(entities);

      force.nodes(graphData.nodes)
          .links(graphData.links)
          .start();

      redrawElements(graphData);
    }

    return directive;
  }]);

})(angular);
