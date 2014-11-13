describe('Graph-Network Directive', function() {
  var scope, element, $httpBackend;

  var additionalPerson = {
    "id": "person8Hugo",
    "displayName": "Hugo",
    "type": "person",
    "rankingValue": 6,
  };
  var additionalRelation = {
    "personAId": "person8Hugo",
    "personBId": "person10Bert",
    "weight": 0.222
  };

  beforeEach(module('vita'));

  beforeEach(inject(function(_$httpBackend_, $rootScope, $compile, TestData, $routeParams) {
    $httpBackend = _$httpBackend_;

    scope = $rootScope.$new();

    $httpBackend.expectGET(new RegExp('/documents/[^/]+/entities/relations[^/]+$')).respond(
            TestData.entityRelations);

    $routeParams.documentId = 'doc-id';
    scope.entities = TestData.graphNetworkEntities;
    scope.rangeStart = 0.0;
    scope.rangeEnd = 1.0;

    element = '<graph-network entities="entities" data-range-begin="rangeStart" data-range-end="rangeEnd"></graph-network>';

    element = $compile(element)(scope);
    $httpBackend.flush();
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

    TestData.entityRelations.entityIds.push(additionalPerson.id);
    TestData.entityRelations.relations.push(additionalRelation);
    $httpBackend.expectGET(new RegExp('/documents/[^/]+/entities/relations[^/]+$')).respond(
            TestData.entityRelations);
    TestData.graphNetworkEntities.push(additionalPerson);

    // Start digestion cycle to trigger the $watch which rebuilds the graph
    element.scope().$apply();
    $httpBackend.flush();

    expect(element.scope().entities.length).toBe(4);
    expect(element.find('.node').length).toBe(initialNodeCount + 1);
    expect(element.find('.link').length).toBe(initialLinkCount + 1);
  }));

  it('should display nothing if the data are undefined', function() {
    $httpBackend.expectGET('/documents/doc-id/entities/relations?entityIds=&rangeEnd=1&rangeStart=0&type=person').respond({
      entityIds: [],
      relations: []
    });

    scope.entities = undefined;
    element.scope().$apply();

    $httpBackend.flush();

    expect(element.find('.node').length).toBe(0);
    expect(element.find('.link').length).toBe(0);
  });

  it('should receive the range change', function() {
    var newRangeStart = 13, newRangeEnd = 77;
    expect(element.scope().rangeStart).not.toEqual(newRangeStart);
    expect(element.scope().rangeEnd).not.toEqual(newRangeEnd);

    scope.rangeStart = newRangeStart;
    scope.rangeEnd = newRangeEnd;

    expect(element.scope().rangeStart).toEqual(newRangeStart);
    expect(element.scope().rangeEnd).toEqual(newRangeEnd);
  });

});
