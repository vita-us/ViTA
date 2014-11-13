(function(angular) {
  'use strict';

  var app = angular.module('vita');

  app.factory('TestData', function() {
    return {
      documents: {
        "totalCount": 5,
        "documents": [{
          "id": "doc13a",
          "metadata": {
            "title": "Rotkaepchen",
            "author": "Hans Mueller",
            "publisher": "XY Verlag",
            "publishYear": 1957,
            "genre": "Fantasy",
            "edition": "Limited Edition"
          },
          "progress": {
            "graphView": {
              "isReady": false,
              "isFailed": false,
              "progress": 0.7123
            },
            "wordCloud": {
              "isReady": true,
              "isFailed": false,
              "progress": 1.0
            },
            "places": {
              "isReady": false,
              "isFailed": true,
              "progress": 0.6123
            },
            "persons": {
              "isReady": false,
              "isFailed": false,
              "progress": 0.001
            },
            "fingerPrints": {
              "isReady": false,
              "isFailed": false,
              "progress": 0.81238
            },
            "text": {
              "isReady": true,
              "isFailed": false,
              "progress": 1.0
            },
            "status": "success"
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
          },
          "progress": {
            "graphView": {
              "isReady": false,
              "isFailed": false,
              "progress": 0.7123
            },
            "wordCloud": {
              "isReady": true,
              "isFailed": false,
              "progress": 1.0
            },
            "places": {
              "isReady": false,
              "isFailed": true,
              "progress": 0.6123
            },
            "persons": {
              "isReady": false,
              "isFailed": false,
              "progress": 0.001
            },
            "fingerPrints": {
              "isReady": false,
              "isFailed": false,
              "progress": 0.81238
            },
            "text": {
              "isReady": true,
              "isFailed": false,
              "progress": 1.0
            },
            "status": "scheduled"
          }
        }, {
          "id": "doc16",
          "metadata": {
            "title": "Rapunzel",
            "author": "Grimm",
            "publisher": "Der Verlag",
            "publishYear": 1928,
            "genre": "Maerchen",
            "edition": "Standard"
          },
          "progress": {
            "graphView": {
              "isReady": false,
              "isFailed": false,
              "progress": 0.7123
            },
            "wordCloud": {
              "isReady": true,
              "isFailed": false,
              "progress": 1.0
            },
            "places": {
              "isReady": false,
              "isFailed": true,
              "progress": 0.6123
            },
            "persons": {
              "isReady": false,
              "isFailed": false,
              "progress": 0.001
            },
            "fingerPrints": {
              "isReady": false,
              "isFailed": false,
              "progress": 0.81238
            },
            "text": {
              "isReady": true,
              "isFailed": false,
              "progress": 1.0
            },
            "status": "failed"
          }
        }, {
          "id": "doc18",
          "metadata": {
            "title": "Schneewittchen",
            "author": "Grimm",
            "publisher": "Der Verlag",
            "publishYear": 1958,
            "genre": "Maerchen",
            "edition": "Standard"
          },
          "progress": {
            "graphView": {
              "isReady": false,
              "isFailed": false,
              "progress": 0.7123
            },
            "wordCloud": {
              "isReady": true,
              "isFailed": false,
              "progress": 1.0
            },
            "places": {
              "isReady": false,
              "isFailed": true,
              "progress": 0.6123
            },
            "persons": {
              "isReady": false,
              "isFailed": false,
              "progress": 0.001
            },
            "fingerPrints": {
              "isReady": false,
              "isFailed": false,
              "progress": 0.81238
            },
            "text": {
              "isReady": true,
              "isFailed": false,
              "progress": 1.0
            },
            "status": "cancelled"
          }
        }, {
          "id": "doc20",
          "metadata": {
            "title": "Rumpelstielzchen",
            "author": "Grimm",
            "publisher": "Der Verlag",
            "publishYear": 1952,
            "genre": "Maerchen",
            "edition": "Standard"
          },
          "progress": {
            "graphView": {
              "isReady": false,
              "isFailed": false,
              "progress": 0.7123
            },
            "wordCloud": {
              "isReady": true,
              "isFailed": false,
              "progress": 1.0
            },
            "places": {
              "isReady": false,
              "isFailed": true,
              "progress": 0.6123
            },
            "persons": {
              "isReady": false,
              "isFailed": false,
              "progress": 0.001
            },
            "fingerPrints": {
              "isReady": false,
              "isFailed": false,
              "progress": 0.81238
            },
            "text": {
              "isReady": true,
              "isFailed": false,
              "progress": 1.0
            },
            "status": "running"
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
        "progress": {
          "graphView": {
            "isReady": false,
            "isFailed": false,
            "progress": 0.7123
          },
          "wordCloud": {
            "isReady": true,
            "isFailed": false,
            "progress": 1.0
          },
          "places": {
            "isReady": false,
            "isFailed": true,
            "progress": 0.6123
          },
          "persons": {
            "isReady": false,
            "isFailed": false,
            "progress": 0.001
          },
          "fingerPrints": {
            "isReady": false,
            "isFailed": false,
            "progress": 0.81238
          },
          "text": {
            "isReady": true,
            "isFailed": false,
            "progress": 1.0
          },
          "status": "success"
        },
        "content": {
          "parts": [{
            "chapters": ["chapter1", "chapter2", "chapter3"],
            "number": 1,
            "title": "Der erste Teil"
          }],
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
            "id": "1.1",
            "title": "Anfang",
            "number": 1,
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
            "id": "1.2",
            "title": "Mittel",
            "number": 2,
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
            "id": "1.3",
            "title": "Ende",
            "number": 3,
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
        "title": "Anfang",
        "number": 13,
        "length": 128438,
        "text": "When Mr. Bilbo Baggins of Bag End announced that he would shortly be celebrating his eleventy-first birthday with a party of special magnificence, there was much talk and excitement in Hobbiton.Bilbo was very rich and very peculiar, and had been the wonder of the Shire for sixty years, ever since his remarkable disappearance and unexpected return. The riches he had brought back from his travels had now become a local legend, and it was popularly believed, whatever the old folk might say, that the Hill at Bag End was full of tunnels stuffed with treasure. And if that was not enough for fame, there was also his prolonged vigour to marvel at. Time wore on, but it seemed to have little effect on Mr. Baggins. At ninety he was much the same as at fifty. At ninety-nine they began to call him well-preserved; but unchanged would have been nearer the mark. There were some that shook their heads and thought this was too much of a good thing; it seemed unfair that anyone should possess (apparently) perpetual youth as well as (reputedly) inexhaustible wealth.‘It will have to be paid for,’ they said. ‘It isn’t natural, and trouble will come of it!’",
        "range": {
          "start": {
            "chapter": "1.13",
            "offset": 0,
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

      secondChapter: {
        "id": "1.13",
        "title": "Seenot",
        "number": 13,
        "length": 128438,
        "text": "‘But what about this Frodo that lives with him?’ asked Old Noakes of Bywater. ‘Baggins is his name, but he’s more than half a Brandybuck, they say. It beats me why any Baggins of Hobbiton should go looking for a wife away there in Buckland, where folks are so queer.’",
        "range": {
          "start": {
            "chapter": "1.13",
            "offset": 1000,
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

      thirdChapter: {
        "id": "1.13",
        "title": "Ende",
        "number": 13,
        "length": 128438,
        "text": "That very month was September, and as fine as you could ask. A day or two later a rumour (probably started by the knowledgeable Sam) was spread about that there were going to be fireworks – fireworks, what is more, such as had not been seen in the Shire for nigh on a century, not indeed since the Old Took died.Days passed and The Day drew nearer. An odd-looking waggon laden with odd-looking packages rolled into Hobbiton one evening and toiled up the Hill to Bag End. The startled hobbits peered out of lamplit doors to gape at it. It was driven by outlandish folk, singing strange songs: dwarves with long beards and deep hoods. A few of them remained at Bag End. At the end of the second week in September a cart came in through Bywater from the direction of Brandywine Bridge in broad daylight. An old man was driving it all alone. He wore a tall pointed blue hat, a long grey cloak, and a silver scarf. He had a long white beard and bushy eyebrows that stuck out beyond the brim of his hat. Small hobbit-children ran after the cart all through Hobbiton and right up the hill. It had a cargo of fireworks, as they rightly guessed. At Bilbo’s front door the old man began to unload: there were great bundles of fireworks of all sorts and shapes, each labelled with a large red G and the elf-rune, .That was Gandalf’s mark, of course, and the old man was Gandalf the Wizard, whose fame in the Shire was due mainly to his skill with fires, smokes, and lights. His real business was far more difficult and dangerous, but the Shire-folk knew nothing about it. To them he was just one of the ‘attractions’ at the Party. Hence the excitement of the hobbit-children. ‘G for Grand!’ they shouted, and the old man smiled. They knew him by sight, though he only appeared in Hobbiton occasionally and never stopped long; but neither they nor any but the oldest of their elders had seen one of his firework displays – they now belonged to a legendary past.",
        "range": {
          "start": {
            "chapter": "1.13",
            "offset": 2000,
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

      relationOccurrences: {
        "occurrences": [{
          "start": {
            "chapter": 1.1,
            "offset": 5,
            "progress": 0
          },
          "end": {
            "chapter": 1.1,
            "offset": 22,
            "progress": 0.05
          },
          "length": 25
        }, {
          "start": {
            "chapter": 1.1,
            "offset": 194,
            "progress": 0.05
          },
          "end": {
            "chapter": 1.1,
            "offset": 199,
            "progress": 0.1
          },
          "length": 15
        }, {
          "start": {
            "chapter": 1.1,
            "offset": 701,
            "progress": 0.05
          },
          "end": {
            "chapter": 1.1,
            "offset": 712,
            "progress": 0.1
          },
          "length": 15
        }, {
          "start": {
            "chapter": 1.2,
            "offset": 1000,
            "progress": 0.2
          },
          "end": {
            "chapter": 1.2,
            "offset": 1077,
            "progress": 0.25
          },
          "length": 30
        }, {
          "start": {
            "chapter": 1.3,
            "offset": 3359,
            "progress": 0.3
          },
          "end": {
            "chapter": 1.3,
            "offset": 3377,
            "progress": 0.35
          },
          "length": 20
        }, {
          "start": {
            "chapter": 1.3,
            "offset": 3100,
            "progress": 0.3
          },
          "end": {
            "chapter": 1.3,
            "offset": 3150,
            "progress": 0.35
          },
          "length": 50
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

      entities: [{
        "id": "bilbo",
        "displayName": "Mr. Bilbo Baggins",
        "type": "person",
        "attributes": [{
          "id": "attr12397",
          "content": "15",
          "type": "age"
        }, {
          "id": "at65657",
          "content": "Mr. Baggins",
          "type": "name"
        }, {
          "id": "at65657",
          "content": "Bilbo",
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
      }, {
        "id": "person9",
        "displayName": "Frodo",
        "type": "person",
        "attributes": [],
        "rankingValue": 2,
        "entityRelations": [{
          "relatedEntity": "person10Ben",
          "weight": 0.81234
        }, {
          "relatedEntity": "person18Bert",
          "weight": 0.7345
        }]
      }, {
        "id": "person9",
        "displayName": "Gandalf",
        "type": "person",
        "attributes": [{
          "id": "attr12397",
          "content": "Gandalf the Wizard",
          "type": "name"
        }],
        "rankingValue": 7,
        "entityRelations": [{
          "relatedEntity": "person10Ben",
          "weight": 0.81234
        }, {
          "relatedEntity": "person18Bert",
          "weight": 0.7345
        }]
      }, {
        "id": "person9",
        "displayName": "Old Noakes of Bywater",
        "type": "person",
        "attributes": [],
        "rankingValue": 70,
        "entityRelations": [{
          "relatedEntity": "person10Ben",
          "weight": 0.81234
        }, {
          "relatedEntity": "person18Bert",
          "weight": 0.7345
        }]
      }],

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

      wordcloudhugo: {
        "items": [{
          "word": "Hugo",
          "frequency": 42
        }, {
          "word": "Test",
          "frequency": 23
        }]
      },

      wordcloudbert: {
        "items": [{
          "word": "Bert",
          "frequency": 42
        }, {
          "word": "Word",
          "frequency": 23
        }]
      },

      analysisProgress: {
        "graphView": {
          "isReady": false,
          "isFailed": false,
          "progress": 0.7123
        },
        "wordCloud": {
          "isReady": true,
          "isFailed": false,
          "progress": 1.0
        },
        "places": {
          "isReady": false,
          "isFailed": true,
          "progress": 0.6123
        },
        "persons": {
          "isReady": false,
          "isFailed": false,
          "progress": 0.001
        },
        "fingerPrints": {
          "isReady": false,
          "isFailed": false,
          "progress": 0.81238
        },
        "text": {
          "isReady": true,
          "isFailed": false,
          "progress": 1.0
        },
        "status": "success"
      }

    };

  });
})(angular);
