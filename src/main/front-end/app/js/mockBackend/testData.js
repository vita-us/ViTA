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
          "word": "Courtney",
          "frequency": 88
        }, {
          "word": "Head",
          "frequency": 44
        }, {
          "word": "Stefanie",
          "frequency": 22
        }, {
          "word": "Jeannette",
          "frequency": 11
        }, {
          "word": "Dianna",
          "frequency": 5
        }, {
          "word": "Mays",
          "frequency": 10
        }, {
          "word": "Deleon",
          "frequency": 17
        }, {
          "word": "Maryann",
          "frequency": 34
        }, {
          "word": "Adele",
          "frequency": 66
        }, {
          "word": "Ora",
          "frequency": 23
        }, {
          "word": "Mcbride",
          "frequency": 5
        }, {
          "word": "Avis",
          "frequency": 82
        }, {
          "word": "Bridget",
          "frequency": 72
        }, {
          "word": "Martinez",
          "frequency": 49
        }, {
          "word": "Winnie",
          "frequency": 60
        }, {
          "word": "Blanche",
          "frequency": 19
        }, {
          "word": "Dorthy",
          "frequency": 37
        }, {
          "word": "Gay",
          "frequency": 61
        }, {
          "word": "Duffy",
          "frequency": 86
        }, {
          "word": "Emma",
          "frequency": 73
        }, {
          "word": "Cathryn",
          "frequency": 71
        }, {
          "word": "Taylor",
          "frequency": 19
        }, {
          "word": "Chaney",
          "frequency": 6
        }, {
          "word": "Ayala",
          "frequency": 3
        }, {
          "word": "Alicia",
          "frequency": 94
        }, {
          "word": "Stephens",
          "frequency": 68
        }, {
          "word": "Mueller",
          "frequency": 16
        }, {
          "word": "Berry",
          "frequency": 42
        }, {
          "word": "Cherry",
          "frequency": 28
        }, {
          "word": "Nunez",
          "frequency": 47
        }, {
          "word": "Fletcher",
          "frequency": 28
        }, {
          "word": "Bishop",
          "frequency": 2
        }, {
          "word": "Deloris",
          "frequency": 64
        }, {
          "word": "Watts",
          "frequency": 23
        }]
      },

    };
  });
})(angular);
