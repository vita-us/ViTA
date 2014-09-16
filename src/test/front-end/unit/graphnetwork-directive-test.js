describe('Graph-Network Directive', function() {
  var scope, element;

  beforeEach(module('vita'));

  beforeEach(inject(function($rootScope, $compile, TestData) {

    scope = $rootScope.$new();

    scope.entities = TestData.graphNetworkEntities;
    element = '<graph-network entities="entities"></graph-network>';

    element = $compile(element)(scope);
    scope.$digest();

  }));

  it('should create the correct number of nodes', inject(function(TestData) {
    expect(element.find('.node').length).toBe(TestData.graphNetworkEntities.length);
  }));

  it('should create the only links for possible relations', function() {
    // Too complex to put the logic in here, thats why a static count is used
    expect(element.find('.link').length).toBe(1);
  });

});
