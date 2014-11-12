(function(angular) {
  'use strict';

  var vitaDirectives = angular.module('vitaDirectives');

  vitaDirectives.directive('graphNetwork', [
      'CssClass',
      'EntityRelation',
      '$routeParams',
      function(CssClass, EntityRelation, $routeParams) {

    var directive = {
      replace: false,
      restrict: 'EA',
      scope: {
        entities: '=',
        width: '@',
        height: '@',
        rangeStart: '=',
        rangeEnd: '='
      },
      link: function(scope, element) {
        buildGraph(element, scope);

        scope.$watch('[entities,rangeStart,rangeEnd]', function() {
          fetchRelationsAndDrawElements(scope.entities, scope.rangeStart, scope.rangeEnd);
        }, true);
      }
    };

    var MAXIMUM_LINK_DISTANCE = 100, MINIMUM_LINK_DISTANCE = 40;

    var graph, force, nodes, links;

    function buildGraph(element, scope) {
      var container = d3.select(element[0]);
      var width = scope.width || 800;
      var height = scope.height || 400;

      graph = container.append("svg")
          .classed("graph-network", true)
          .attr("width", width)
          .attr("height", height)
          .append('g'); // an extra group for zooming

      // Order matters - elements of last group are drawn on top
      graph.append('g').attr('id', "linkGroup");
      graph.append('g').attr('id', "nodeGroup");

      force = d3.layout.force()
          .size([width, height])
          .charge(-200)
          .gravity(0.025)
          .linkDistance(calculateLinkDistance)
          .on('tick', setNewPositions);
    }

    function fetchRelationsAndDrawElements(entities, rangeStart, rangeEnd) {
      // Handle undefined data as empty dataset
      entities = entities || [];

      var entityIds = entities.map(function(entity) {
        return entity.id;
      });

      EntityRelation.get({
        documentId: $routeParams.documentId,
        entityIds: entityIds.join(','),
        rangeStart: rangeStart,
        rangeEnd: rangeEnd,
        type: 'person'
      }, function(relationData) {
        var graphData = parseEntitiesToGraphData(entities, relationData);

        redrawElements(graphData);

        force.nodes(graphData.nodes)
            .links(graphData.links)
            .start();
      });
    }

    function parseEntitiesToGraphData(entities, relationData) {
      var entityIdNodeMap = mapEntitiesToNodes(entities, relationData.entityIds);

      var links = [];
      var relations = relationData.relations;

      for (var i = 0, l = relations.length; i < l; i++) {
        var relation = relations[i];

        links.push(createLinkFromRelation(relation, entityIdNodeMap));
      }

      return {
        nodes: entityIdNodeMap.values(),
        links: links
      };
    }

    function mapEntitiesToNodes(entities, idsOfDisplayedEntities) {
      var nodeMap = d3.map();

      for (var i = 0, l = idsOfDisplayedEntities.length; i < l; i++) {
        var entityId = idsOfDisplayedEntities[i];

        // Create nodes for displayed entities
        nodeMap.set(entityId, {
          id: entityId
        });
      }

      // Add additional data of the entities
      for (var i = 0, l = entities.length; i < l; i++) {
        var entity = entities[i];

        if (nodeMap.has(entity.id)) {
          var entityNode = nodeMap.get(entity.id);

          entityNode.displayName = entity.displayName;
          entityNode.rankingValue = entity.rankingValue;
          entityNode.type = entity.type;
        }
      }

      return nodeMap;
    }

    function createLinkFromRelation(relation, entityIdNodeMap) {
      return {
        source: entityIdNodeMap.get(relation.personAId),
        target: entityIdNodeMap.get(relation.personBId),
        weight: relation.weight
      }
    }

    function calculateLinkDistance(link) {
      var variableDistance = MAXIMUM_LINK_DISTANCE - MINIMUM_LINK_DISTANCE;
      return MINIMUM_LINK_DISTANCE + variableDistance * link.weight;
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

    function redrawElements(graphData) {
      /*
       * Remove all elements because they are redrawn. This is the only solution
       * currently because it isn't guaranteed, that the controller is passing
       * the same objects for the same displayed entities. For example one
       * entity might disappear, but this directive receives completely new
       * objects - even for unchanged entities.
       */

      links = graph.select('#linkGroup').selectAll('.link')
          .data(graphData.links);

      links.exit().remove();
      links.enter().append('line').classed('link', true);

      nodes = graph.select('#nodeGroup').selectAll('.node')
          .data(graphData.nodes);

      nodes.exit().remove();
      nodes.enter().append('circle')
          .attr("class", function(d) {
            return CssClass.forRankingValue(d.rankingValue);
          })
          .classed('node', true)
          .attr('r', 20)
          .call(force.drag);
    }

    return directive;
  }]);

})(angular);
