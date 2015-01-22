(function(angular) {
  'use strict';

  var app = angular.module('vitaFilters');

  app.filter('entityNameFilter', function() {
    return function(entities, query) {
      if (!(entities instanceof Array)) {
        return entities;
      }

      resetPreviousModifications(entities);

      if (!query) {
        return entities;
      }

      var filteredEntities = [];
      for (var i = 0; i < entities.length; i++) {
        var entity = entities[i];

        // Look for the query string case insensitive
        if (containsQueryCaseInsensitive(entity.displayName, query)) {
          filteredEntities.push(entity);
        }

        // Search in attributes for alternative names
        for (var j = 0; j < entity.attributes.length; j++) {
          var attribute = entity.attributes[j];
          if (attribute.attributetype !== 'name') {
            continue;
          }

          if (containsQueryCaseInsensitive(attribute.content, query)) {
            if (filteredEntities.indexOf(entity) < 0) {
              entity.alternativeName = attribute.content;
              filteredEntities.push(entity);
            }
          }
        }
      }

      return filteredEntities;
    };

    function resetPreviousModifications(entities) {
      for (var i = 0, l = entities.length; i < l; i++) {
        entities[i].alternativeName = undefined;
      }
    }

    function containsQueryCaseInsensitive(text, query) {
      var lowerCaseText = text.toLowerCase();
      var lowerCaseQuery = query.toLowerCase();

      return lowerCaseText.indexOf(lowerCaseQuery) > -1;
    }
  });

})(angular);
