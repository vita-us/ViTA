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

  it('should update when the underlying data changes', inject(function(TestData) {
    var initialNodeCount = element.find('.node').length;
    var initialLinkCount = element.find('.link').length;

    TestData.graphNetworkEntities.push(TestData.singlePerson);
    // Start digestion cycle to trigger the $watch which rebuilds the graph
    element.scope().$apply();

    expect(element.scope().entities.length).toBe(4);
    expect(element.find('.node').length).toBe(initialNodeCount + 1);
    expect(element.find('.link').length).toBe(initialLinkCount + 1);
  }));

  it('should display nothing if the data are undefined', function() {
    scope.entities = undefined;
    element.scope().$apply();

    expect(element.find('.node').length).toBe(0);
    expect(element.find('.link').length).toBe(0);
  });

});
