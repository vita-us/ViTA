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

      parts: {
        "totalCount": "15",
        "parts": [{
          "number": 1,
          "title": "Der erste Teil",
          "chapters": [{
            "id": "1.13",
            "title": "Seenot",
            "number": 13,
            "length": 242341,
            "range": {
              "start": {
                "chapter": "1.13",
                "offset": 13,
                "progress": 0
              },
              "end": {
                "chapter": "1.13",
                "offset": 29,
                "progress": 0.237261231
              },
              "length": 14123
            }
          }, {
            "id": "1.14",
            "title": "Baum",
            "number": 14,
            "length": 581234,
            "range": {
              "start": {
                "chapter": "1.14",
                "offset": 123,
                "progress": 0.29
              },
              "end": {
                "chapter": "1.13",
                "offset": 29888,
                "progress": 0.487261231
              },
              "length": 82342
            }
          }, {
            "id": "1.15",
            "title": "Seenot am Ende",
            "number": 15,
            "length": 8215,
            "range": {
              "start": {
                "chapter": "1.15",
                "offset": 115,
                "progress": 0.682381
              },
              "end": {
                "chapter": "1.15",
                "offset": 229,
                "progress": 0.87261231
              },
              "length": 14123
            }
          }]
        }]
      },

      singleChapter: {
        "id": "1.13",
        "title": "Seenot",
        "number": 13,
        "length": 128438,
        "text": "gaaaaanz ... viiiiieleeeee ... Woeerteeer, Saetze, etc.",
        "range": {
          "start": {
            "chapter": "1.13",
            "offset": 13,
            "progress": 0.182381
          },
          "end": {
            "chapter": "1.13",
            "offset": 29,
            "progress": 0.87261231
          },
          "length": 14123
        }
      },

      fingerprint: {
        "occurrences": [{
          "start": {
            "chapter": 1.18,
            "offset": 13,
            "progress": 0
          },
          "end": {
            "chapter": 1.18,
            "offset": 29,
            "progress": 0.05
          },
          "length": 14123
        }, {
          "start": {
            "chapter": 1.18,
            "offset": 13,
            "progress": 0.05
          },
          "end": {
            "chapter": 1.18,
            "offset": 29,
            "progress": 0.1
          },
          "length": 14123
        }, {
          "start": {
            "chapter": 1.18,
            "offset": 13,
            "progress": 0.2
          },
          "end": {
            "chapter": 1.18,
            "offset": 29,
            "progress": 0.25
          },
          "length": 14123
        }, {
          "start": {
            "chapter": 1.18,
            "offset": 13,
            "progress": 0.3
          },
          "end": {
            "chapter": 1.18,
            "offset": 29,
            "progress": 0.35
          },
          "length": 14123
        }, {
          "start": {
            "chapter": 1.18,
            "offset": 13,
            "progress": 0.5
          },
          "end": {
            "chapter": 1.18,
            "offset": 29,
            "progress": 0.55
          },
          "length": 14123
        }, {
          "start": {
            "chapter": 1.18,
            "offset": 13,
            "progress": 0.65
          },
          "end": {
            "chapter": 1.18,
            "offset": 29,
            "progress": 0.7
          },
          "length": 14123
        }, {
          "start": {
            "chapter": 1.18,
            "offset": 13,
            "progress": 0.7
          },
          "end": {
            "chapter": 1.18,
            "offset": 29,
            "progress": 0.75
          },
          "length": 14123
        }, {
          "start": {
            "chapter": 1.18,
            "offset": 13,
            "progress": 0.8
          },
          "end": {
            "chapter": 1.18,
            "offset": 29,
            "progress": 0.85
          },
          "length": 14123
        }, {
          "start": {
            "chapter": 1.18,
            "offset": 13,
            "progress": 0.85
          },
          "end": {
            "chapter": 1.18,
            "offset": 29,
            "progress": 0.90
          },
          "length": 14123
        }, {
          "start": {
            "chapter": 1.18,
            "offset": 13,
            "progress": 0.95
          },
          "end": {
            "chapter": 1.18,
            "offset": 29,
            "progress": 1
          },
          "length": 14123
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

      analysisProgress: {
        "graphView": {
          "isReady": false,
          "progress": 0.7123
        },
        "wordCloud": {
          "isReady": true,
          "progress": 1.0
        },
        "places": {
          "isReady": false,
          "progress": 0.6123
        },
        "persons": {
          "isReady": false,
          "progress": 0.001
        },
        "fingerPrints": {
          "isReady": false,
          "progress": 0.81238
        },
        "text": {
          "isReady": true,
          "progress": 1.0
        }
      }
    };

  });
})(angular);
