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
          "title": "DerersteTeil",
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
            "title": "SeenotamEnde",
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

      fingerprint: {
        "occurrences": [{
          "start": {
            "chapter": 1.18,
            "offset": 13,
            "progress": 0.0626
          },
          "end": {
            "chapter": 1.18,
            "offset": 29,
            "progress": 0.0701
          },
          "length": 14123
        }, {
          "start": {
            "chapter": 1.18,
            "offset": 13,
            "progress": 0.086
          },
          "end": {
            "chapter": 1.18,
            "offset": 29,
            "progress": 0.0892
          },
          "length": 14123
        }, {
          "start": {
            "chapter": 1.18,
            "offset": 13,
            "progress": 0.1534
          },
          "end": {
            "chapter": 1.18,
            "offset": 29,
            "progress": 0.173
          },
          "length": 14123
        }, {
          "start": {
            "chapter": 1.18,
            "offset": 13,
            "progress": 0.1931
          },
          "end": {
            "chapter": 1.18,
            "offset": 29,
            "progress": 0.2281
          },
          "length": 14123
        }, {
          "start": {
            "chapter": 1.18,
            "offset": 13,
            "progress": 0.218
          },
          "end": {
            "chapter": 1.18,
            "offset": 29,
            "progress": 0.2227
          },
          "length": 14123
        }, {
          "start": {
            "chapter": 1.18,
            "offset": 13,
            "progress": 0.2992
          },
          "end": {
            "chapter": 1.18,
            "offset": 29,
            "progress": 0.3327
          },
          "length": 14123
        }, {
          "start": {
            "chapter": 1.18,
            "offset": 13,
            "progress": 0.3527
          },
          "end": {
            "chapter": 1.18,
            "offset": 29,
            "progress": 0.392
          },
          "length": 14123
        }, {
          "start": {
            "chapter": 1.18,
            "offset": 13,
            "progress": 0.368
          },
          "end": {
            "chapter": 1.18,
            "offset": 29,
            "progress": 0.3992
          },
          "length": 14123
        }, {
          "start": {
            "chapter": 1.18,
            "offset": 13,
            "progress": 0.4477
          },
          "end": {
            "chapter": 1.18,
            "offset": 29,
            "progress": 0.4921
          },
          "length": 14123
        }, {
          "start": {
            "chapter": 1.18,
            "offset": 13,
            "progress": 0.4752
          },
          "end": {
            "chapter": 1.18,
            "offset": 29,
            "progress": 0.4798
          },
          "length": 14123
        }, {
          "start": {
            "chapter": 1.18,
            "offset": 13,
            "progress": 0.4829
          },
          "end": {
            "chapter": 1.18,
            "offset": 29,
            "progress": 0.5356
          },
          "length": 14123
        }, {
          "start": {
            "chapter": 1.18,
            "offset": 13,
            "progress": 0.4891
          },
          "end": {
            "chapter": 1.18,
            "offset": 29,
            "progress": 0.5153
          },
          "length": 14123
        }, {
          "start": {
            "chapter": 1.18,
            "offset": 13,
            "progress": 0.6351
          },
          "end": {
            "chapter": 1.18,
            "offset": 29,
            "progress": 0.6443
          },
          "length": 14123
        }, {
          "start": {
            "chapter": 1.18,
            "offset": 13,
            "progress": 0.6652
          },
          "end": {
            "chapter": 1.18,
            "offset": 29,
            "progress": 0.68
          },
          "length": 14123
        }, {
          "start": {
            "chapter": 1.18,
            "offset": 13,
            "progress": 0.6915
          },
          "end": {
            "chapter": 1.18,
            "offset": 29,
            "progress": 0.7176
          },
          "length": 14123
        }, {
          "start": {
            "chapter": 1.18,
            "offset": 13,
            "progress": 0.6953
          },
          "end": {
            "chapter": 1.18,
            "offset": 29,
            "progress": 0.7287
          },
          "length": 14123
        }, {
          "start": {
            "chapter": 1.18,
            "offset": 13,
            "progress": 0.7583
          },
          "end": {
            "chapter": 1.18,
            "offset": 29,
            "progress": 0.7688
          },
          "length": 14123
        }, {
          "start": {
            "chapter": 1.18,
            "offset": 13,
            "progress": 0.8525
          },
          "end": {
            "chapter": 1.18,
            "offset": 29,
            "progress": 0.8771
          },
          "length": 14123
        }, {
          "start": {
            "chapter": 1.18,
            "offset": 13,
            "progress": 0.951
          },
          "end": {
            "chapter": 1.18,
            "offset": 29,
            "progress": 0.9784
          },
          "length": 14123
        }, {
          "start": {
            "chapter": 1.18,
            "offset": 13,
            "progress": 0.9583
          },
          "end": {
            "chapter": 1.18,
            "offset": 29,
            "progress": 0.9876
          },
          "length": 14123
        }]
      },
    };
  });
})(angular);
