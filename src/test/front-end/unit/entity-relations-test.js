describe('EntityRelationService', function() {

  beforeEach(function() {
    this.addMatchers({
      toEqualData: function(expected) {
        return angular.equals(this.actual, expected);
      }
    });
  });

  beforeEach(module('vita'));

  it('should return the entity relations', inject(function($httpBackend, EntityRelation, TestData) {
    $httpBackend.expectGET('webapi/documents/doc13a/entities/relations?entityIds=pers1,pers2,pers3'
                    + '&rangeEnd=0.456&rangeStart=0.123&type=person')
                    .respond(TestData.entityRelations);

    var result = EntityRelation.get({
      documentId: 'doc13a',
      entityIds: ['pers1', 'pers2', 'pers3'].join(','),
      rangeStart: '0.123',
      rangeEnd: '0.456',
      type: 'person'
    });

    $httpBackend.flush();

    expect(result).toEqualData(TestData.entityRelations);
  }));

});
