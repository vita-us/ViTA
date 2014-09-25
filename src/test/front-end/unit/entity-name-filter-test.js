describe('EntityNameFilter', function() {
  var $filter, entities;

  beforeEach(function() {
    module('vita');

    inject(function(TestData, _$filter_) {
      entities = TestData.places.places;
      $filter = _$filter_;
    });
  });

  it('should filter case insensitive', function() {
    expect(entities.length).toBe(2);
    var query = 'pa';

    var filteredEntities = $filter('entityNameFilter')(entities, query);

    expect(filteredEntities.length).toBe(1);
    expect(filteredEntities[0].displayName).toBe('Paris');
  });

  it('should look anywhere in the searched string for the query', function() {
    expect(entities.length).toBe(2);
    var query = 'bur';

    var filteredEntities = $filter('entityNameFilter')(entities, query);

    expect(filteredEntities.length).toBe(1);
    expect(filteredEntities[0].displayName).toBe('Hamburg');
  });

});
