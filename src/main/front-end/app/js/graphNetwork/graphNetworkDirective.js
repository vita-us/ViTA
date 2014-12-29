(function(angular) {
  'use strict';

  var vitaDirectives = angular.module('vitaDirectives');

  vitaDirectives.directive('graphNetwork', [
      'CssClass',
      'EntityRelation',
      '$routeParams',
      function(CssClass, EntityRelation, $routeParams) {

    var MINIMUM_GRAPH_WIDTH = 300, MINIMUM_GRAPH_HEIGHT = 300;

    // rangeBEGIN, because Start seems to be an angular keyword
    var directive = {
      restrict: 'A',
      scope: {
        entities: '=',
        width: '=',
        height: '=',
        rangeBegin: '=',
        rangeEnd: '=',
        showFingerprint: '&'
      },
      link: function(scope, element) {
        buildGraph(element, scope.width, scope.height);

        scope.$watch('[entities,rangeBegin,rangeEnd]', function() {
          fetchRelationsAndDrawElements(scope.entities, scope.rangeBegin, scope.rangeEnd,
                  scope.showFingerprint);
        }, true);

        scope.$watch('[width,height]', function(newValues, oldValues) {
          if (!angular.equals(newValues, oldValues)) {
            var newWidth = newValues[0] || MINIMUM_GRAPH_WIDTH;
            var newHeight = newValues[1] || MINIMUM_GRAPH_HEIGHT;
            updateSize(newWidth, newHeight);
          }
        }, true);
      }
    };

    var radiusScale = d3.scale.linear()
        .range([20, 40]);

    var linkWidthScale = d3.scale.linear()
        .domain([0, 1])
        .range([4, 16]);

    var VISIBLE_LINK_LENGTH = 160;

    var graph, force, nodes, links, drag, svgContainer, entityIdNodeMap = d3.map();

    function buildGraph(element, width, height) {
      var container = d3.select(element[0]);
      width = width || MINIMUM_GRAPH_WIDTH;
      height = height || MINIMUM_GRAPH_HEIGHT;

      // Set the zoom with its min and max magnifications
      var zoom = d3.behavior.zoom()
          .scaleExtent([0.25, 2])
          .on('zoom', zoomed);

      drag = d3.behavior.drag()
          .origin(function(d) {
            return d;
          })
          .on('dragstart', function(d) {
            // Prevent panning when dragging a node
            d3.event.sourceEvent.stopPropagation();
            d.fixed = true;
          })
          .on('drag', function(d) {
            d.px = d3.event.x;
            d.py = d3.event.y;
            force.resume();
          })
          .on('dragend', function(d) {
            d.fixed = false;
          });

      svgContainer = container.append('svg')
          .classed('graph-network', true)
          .attr('width', width)
          .attr('height', height)
          .call(zoom);

      // Encapsulate the graph in a group for easier zooming and dragging
      graph = svgContainer.append('g');

      // Order matters - elements of last group are drawn on top
      graph.append('g').attr('id', 'linkGroup');
      graph.append('g').attr('id', 'nodeGroup');

      force = d3.layout.force()
          .size([width, height])
          .charge(-800)
          .gravity(0.04)
          .linkDistance(calculateLinkDistance)
          .linkStrength(0.2)
          .on('tick', setNewPositions);
    }

    function zoomed() {
      graph.attr('transform', 'translate(' + d3.event.translate + ')scale(' + d3.event.scale + ')');
    }

    function fetchRelationsAndDrawElements(entities, rangeStart, rangeEnd, showFingerprint) {
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
        if (!isEntityRelationResponseValid(entities, relationData)) {
          return;
        }

        var graphData = parseEntitiesToGraphData(entities, relationData);

        redrawElements(graphData, showFingerprint);

        force.nodes(graphData.nodes)
            .links(graphData.links)
            .start();
      });
    }

    function isEntityRelationResponseValid(entities, relationData) {
      var receivedEntityIds = relationData.entityIds;
      if (entities.length !== receivedEntityIds.length) {
        return false;
      }

      for (var i = 0, l = entities.length; i < l; i++) {
        var entityId = entities[i].id;
        if (receivedEntityIds.indexOf(entityId) < 0) {
          return false;
        }
      }

      return true;
    }

    function parseEntitiesToGraphData(entities, relationData) {
      updateRadiusScale(entities);
      updateEntityNodeMap(entities, relationData.entityIds);

      var links = [];
      var relations = relationData.relations;

      for (var i = 0, l = relations.length; i < l; i++) {
        var relation = relations[i];

        links.push(createLinkFromRelation(relation));
      }

      return {
        nodes: entityIdNodeMap.values(),
        links: links
      };
    }

    function updateEntityNodeMap(newEntities, idsOfDisplayedEntities) {
      var i, l;

      // Delete removed nodes also from entity map
      var currentIds = entityIdNodeMap.keys();

      for (i = 0, l = currentIds.length; i < l; i++) {
        var id = currentIds[i];
        if (idsOfDisplayedEntities.indexOf(id) < 0) {
          entityIdNodeMap.remove(id);
        }
      }

      // Create nodes for all new entities
      for (i = 0, l = idsOfDisplayedEntities.length; i < l; i++) {
        var newId = idsOfDisplayedEntities[i];

        if (!entityIdNodeMap.has(newId)) {
          entityIdNodeMap.set(newId, {
            id: newId
          });
        }
      }

      // Add additional data of the entities
      for (i = 0, l = newEntities.length; i < l; i++) {
        var entity = newEntities[i];

        // entity might be selected but doesn't occur in the selected range -> not displayed
        if (entityIdNodeMap.has(entity.id)) {
          var entityNode = entityIdNodeMap.get(entity.id);
          entityNode.displayName = entity.displayName;
          entityNode.rankingValue = entity.rankingValue;
          entityNode.type = entity.type;
          entityNode.radius = radiusScale(avoidVisualLie(entity.frequency));
        }
      }
    }

    /**
     * Take the square root because otherwise we would create a visual lie.
     * Double frequency means double area but not double radius.
     * @param frequency
     * @returns {number}
     */
    function avoidVisualLie(frequency) {
      return Math.sqrt(frequency);
    }

    function createLinkFromRelation(relation) {
      return {
        source: entityIdNodeMap.get(relation.entityAId),
        target: entityIdNodeMap.get(relation.entityBId),
        weight: relation.weight
      };
    }

    function updateRadiusScale(entities) {
      var minAndMaxFrequencies = d3.extent(entities, function(entity) {
        return entity.frequency;
      });
      var min = avoidVisualLie(minAndMaxFrequencies[0]);
      var max = avoidVisualLie(minAndMaxFrequencies[1]);
      radiusScale.domain([min, max]);
    }

    function calculateLinkDistance(link) {
      /* The links start from the center of a node.
       * That's why we add the radius of both nodes to let them look equally long. */
      return link.source.radius + VISIBLE_LINK_LENGTH + link.target.radius;
    }

    function setNewPositions() {
      nodes.attr('transform', function(d) {
        return 'translate(' + d.x + ',' + d.y + ')';
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

    function redrawElements(graphData, showFingerprint) {
      links = graph.select('#linkGroup').selectAll('.link')
          .data(graphData.links, function(link) {
            // Links are uniquely identified by these three attributes
            return link.source.id + link.target.id + link.weight;
          });

      links.exit().remove();
      links.enter().append('line')
          .classed('link', true)
          .style('stroke-width', function(d) {
            return linkWidthScale(d.weight) + "px";
          })
          .on('click', function(link) {
            if (showFingerprint instanceof Function) {
              showFingerprint({ids: [link.source.id, link.target.id]});
            }
          });

      nodes = graph.select('#nodeGroup')
          .selectAll('.node-container')
          .data(graphData.nodes,
              function(node) {
                return node.id;
              });

      nodes.exit().remove();
      var newNodes = nodes.enter().append('g').classed('node-container', true).call(drag);

      newNodes.append('circle')
          .attr('class', function(d) {
            return CssClass.forRankingValue(d.rankingValue);
          })
          .classed('node', true)
          .attr('r', function(d) {
            return d.radius;
          });

      var labelGroups = newNodes.append('g').classed('node-label', true);

      // we need to draw the labels first or we cant get the bbox for the background
      labelGroups.append('text')
          .classed('label-text', true)
          .text(function(d) {
            return d.displayName;
          });

      labelGroups.each(function() {
        var labelGroup = d3.select(this);
        var label = labelGroup.select('text');

        // display the label shortly or we cant get the bounding box
        labelGroup.style('display', 'block');
        var labelBBox = label.node().getBBox();
        labelGroup.style('display', undefined);

        labelGroup.append('rect')
            .classed('label-background', true)
            .attr('x', -labelBBox.width / 2)
            .attr('y', -labelBBox.height / 2)
            .attr('width', labelBBox.width)
            .attr('height', labelBBox.height);

        // place the text on top
        labelGroup.node().appendChild(label.node());
      });
    }

    function updateSize(width, height) {
      svgContainer.attr('width', width).attr('height', height);
      force.size([width, height]).start();
    }

    return directive;
  }]);

})(angular);
