(function(angular) {
  'use strict';

  var app = angular.module('vita');

  app.factory('TestData', function() {
    return {
      documents: {
        "totalCount": 2,
        "documents": [{
          "id": "doc13a",
          "metadata": {
            "title": "Rotkaepchen",
            "author": "Hans Mueller",
            "publisher": "XY Verlag",
            "publishYear": 1957,
            "genre": "Fantasy",
            "edition": "Limited Edition"
          }
        }, {
          "id": "doc14",
          "metadata": {
            "title": "Hans guck in die Luft",
            "author": "Peter Mayer",
            "publisher": "ABC Verlag",
            "publishYear": 1968,
            "genre": "Krimi",
            "edition": "Standard"
          }
        }]
      },

      singleDocument: {
        "id": "doc13a",
        "metadata": {
          "title": "Rotkaeppchen",
          "author": "Hans Mueller",
          "publisher": "XY Verlag",
          "publishYear": 1957,
          "genre": "Thriller",
          "edition": "Limited Edition"
        },
        "metrics": {
          "characterCount": 19231,
          "chapterCount": 3,
          "personCount": 8,
          "placeCount": 2,
          "wordCount": 1999
        },
        "content": {
          "parts": {
            "chapters": ["chapter1", "chapter2", "chapter3"],
            "number": 1,
            "title": "Der erste Teil"
          },
          "persons": ["person8Ben", "person10Bert"],
          "places": ["place2Paris", "place10Mordor"]
        }
      },

      places: {
        "totalCount": 2,
        "places": [{
          "id": "place10Paris",
          "displayName": "Paris",
          "type": "place",
          "rankingValue": 1
        }, {
          "id": "place6Hamburg",
          "displayName": "Hamburg",
          "type": "place",
          "rankingValue": 2
        }]
      },

      singlePlace: {
        "id": "place3Hamburg",
        "displayName": "Hamburg",
        "type": "place",
        "attributes": [{
          "id": "attr12697",
          "content": "1700km2",
          "type": "unknown"
        }, {
          "id": "at65157",
          "content": "North Germany",
          "type": "unknown"
        }],
        "rankingValue": 3,
        "entityRelations": [{
          "relatedEntity": "person10Ben",
          "weight": 0.81234
        }, {
          "relatedEntity": "person18Bert",
          "weight": 0.7345
        }]
      },

      singlePerson: {
        "id": "person8Hugo",
        "displayName": "Hugo",
        "type": "person",
        "attributes": [{
          "id": "attr12397",
          "content": "15",
          "type": "age"
        }, {
          "id": "at65657",
          "content": "Hugo Martin",
          "type": "name"
        }],
        "rankingValue": 3,
        "entityRelations": [{
          "relatedEntity": "person10Ben",
          "weight": 0.81234
        }, {
          "relatedEntity": "person18Bert",
          "weight": 0.7345
        }]
      },

      persons: {
        "totalCount": 2,
        "persons": [{
          "id": "person8Hugo",
          "displayName": "Hugo",
          "type": "person",
          "rankingValue": 3
        }, {
          "id": "person10Bert",
          "displayName": "Bert",
          "type": "person",
          "rankingValue": 7
        }]
      },

      graphNetworkEntities: [{
        "id": "person8Andi",
        "displayName": "Andi",
        "type": "person",
        "attributes": [{
          "id": "attr12397",
          "content": "15",
          "type": "age"
        }, {
          "id": "at65657",
          "content": "Andreas",
          "type": "name"
        }],
        "rankingValue": 3,
        "entityRelations": [{
          "relatedEntity": "person10Bert",
          "weight": 0.81234
        }, {
          "relatedEntity": "person18Ben",
          "weight": 0.7345
        }]
      }, {
        "id": "person10Bert",
        "displayName": "Bert",
        "type": "person",
        "attributes": [{
          "id": "attr1041",
          "content": "22",
          "type": "age"
        }, {
          "id": "at7214",
          "content": "Bert Bauer",
          "type": "name"
        }],
        "rankingValue": 2,
        "entityRelations": [{
          "relatedEntity": "person8Hugo",
          "weight": 0.222
        }]
      }, {
        "id": "person61Nobody",
        "displayName": "Nobody",
        "type": "person",
        "attributes": [{
          "id": "at542",
          "content": "Nobo Dy",
          "type": "name"
        }],
        "rankingValue": 25,
        "entityRelations": []
      }],

      wordcloud: {
        "items": [{
          "word": "Ring",
          "frequency": 95
        }, {
          "word": "Time",
          "frequency": 44
        }, {
          "word": "Sword",
          "frequency": 22
        }, {
          "word": "Darkness",
          "frequency": 11
        }, {
          "word": "River",
          "frequency": 5
        }, {
          "word": "Fear",
          "frequency": 10
        }, {
          "word": "Mountains",
          "frequency": 17
        }, {
          "word": "Company",
          "frequency": 34
        }, {
          "word": "Hands",
          "frequency": 66
        }, {
          "word": "Together",
          "frequency": 23
        }, {
          "word": "Beyond",
          "frequency": 5
        }, {
          "word": "Road",
          "frequency": 82
        }, {
          "word": "Words",
          "frequency": 72
        }, {
          "word": "Night",
          "frequency": 49
        }, {
          "word": "End",
          "frequency": 60
        }, {
          "word": "Against",
          "frequency": 19
        }, {
          "word": "Door",
          "frequency": 37
        }, {
          "word": "Upon",
          "frequency": 61
        }, {
          "word": "Fire",
          "frequency": 86
        }, {
          "word": "Folk",
          "frequency": 73
        }, {
          "word": "Soon",
          "frequency": 71
        }, {
          "word": "Suddenly",
          "frequency": 19
        }, {
          "word": "Voice",
          "frequency": 6
        }, {
          "word": "Moment",
          "frequency": 3
        }, {
          "word": "Above",
          "frequency": 64
        }, {
          "word": "Gate",
          "frequency": 68
        }, {
          "word": "Looking",
          "frequency": 16
        }, {
          "word": "Indeed",
          "frequency": 42
        }, {
          "word": "North",
          "frequency": 28
        }, {
          "word": "Ran",
          "frequency": 47
        }, {
          "word": "Clear",
          "frequency": 28
        }, {
          "word": "Riders",
          "frequency": 2
        }, {
          "word": "Since",
          "frequency": 64
        }, {
          "word": "Gone",
          "frequency": 23
        }, {
          "word": "Light",
          "frequency": 33
        }, {
          "word": "Away",
          "frequency": 53
        }, {
          "word": "About",
          "frequency": 17
        }, {
          "word": "Behind",
          "frequency": 41
        }, {
          "word": "Back",
          "frequency": 49
        }, {
          "word": "Go",
          "frequency": 63
        }]
      },

    };
  });
})(angular);
