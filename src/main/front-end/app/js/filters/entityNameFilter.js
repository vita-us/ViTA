(function(angular) {
  'use strict';

  var app = angular.module('vitaFilters');

  app.filter('entityNameFilter', function() {
    return function(entities, query) {
      if (!query || !(entities instanceof Array)) {
        return entities;
      }

      var filteredEntities = [];
      for (var i = 0, l = entities.length; i < l; i++) {
        var entity = entities[i];

        if (entity.displayName.indexOf(query) > -1) {
          filteredEntities.push(entity);
        }
      }

      return filteredEntities;
    };
  });

})(angular);
