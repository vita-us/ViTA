(function(angular) {
  'use strict';

  var app = angular.module('vita');

  app.factory('TestData', function() {
    return {
      documents: {
        'totalCount': 5,
        'documents': [{
          'id': 'doc13a',
          'metadata': {
            'title': 'Rotkaepchen',
            'author': 'Hans Mueller',
            'publisher': 'XY Verlag',
            'publishYear': 1957,
            'genre': 'Fantasy',
            'edition': 'Limited Edition'
          },
          'progress': {
            'graphView': {
              'isReady': false,
              'isFailed': false,
              'progress': 0.7123
            },
            'wordCloud': {
              'isReady': true,
              'isFailed': false,
              'progress': 1.0
            },
            'places': {
              'isReady': false,
              'isFailed': true,
              'progress': 0.6123
            },
            'persons': {
              'isReady': false,
              'isFailed': false,
              'progress': 0.001
            },
            'fingerPrints': {
              'isReady': false,
              'isFailed': false,
              'progress': 0.81238
            },
            'text': {
              'isReady': true,
              'isFailed': false,
              'progress': 1.0
            },
            'status': 'success'
          }
        }, {
          'id': 'doc14',
          'metadata': {
            'title': 'Hans guck in die Luft',
            'author': 'Peter Mayer',
            'publisher': 'ABC Verlag',
            'publishYear': 1968,
            'genre': 'Krimi',
            'edition': 'Standard'
          },
          'progress': {
            'graphView': {
              'isReady': false,
              'isFailed': false,
              'progress': 0.7123
            },
            'wordCloud': {
              'isReady': true,
              'isFailed': false,
              'progress': 1.0
            },
            'places': {
              'isReady': false,
              'isFailed': true,
              'progress': 0.6123
            },
            'persons': {
              'isReady': false,
              'isFailed': false,
              'progress': 0.001
            },
            'fingerPrints': {
              'isReady': false,
              'isFailed': false,
              'progress': 0.81238
            },
            'text': {
              'isReady': true,
              'isFailed': false,
              'progress': 1.0
            },
            'status': 'scheduled'
          }
        }, {
          'id': 'doc16',
          'metadata': {
            'title': 'Rapunzel',
            'author': 'Grimm',
            'publisher': 'Der Verlag',
            'publishYear': 1928,
            'genre': 'Maerchen',
            'edition': 'Standard'
          },
          'progress': {
            'graphView': {
              'isReady': false,
              'isFailed': false,
              'progress': 0.7123
            },
            'wordCloud': {
              'isReady': true,
              'isFailed': false,
              'progress': 1.0
            },
            'places': {
              'isReady': false,
              'isFailed': true,
              'progress': 0.6123
            },
            'persons': {
              'isReady': false,
              'isFailed': false,
              'progress': 0.001
            },
            'fingerPrints': {
              'isReady': false,
              'isFailed': false,
              'progress': 0.81238
            },
            'text': {
              'isReady': true,
              'isFailed': false,
              'progress': 1.0
            },
            'status': 'failed'
          }
        }, {
          'id': 'doc18',
          'metadata': {
            'title': 'Schneewittchen',
            'author': 'Grimm',
            'publisher': 'Der Verlag',
            'publishYear': 1958,
            'genre': 'Maerchen',
            'edition': 'Standard'
          },
          'progress': {
            'graphView': {
              'isReady': false,
              'isFailed': false,
              'progress': 0.7123
            },
            'wordCloud': {
              'isReady': true,
              'isFailed': false,
              'progress': 1.0
            },
            'places': {
              'isReady': false,
              'isFailed': true,
              'progress': 0.6123
            },
            'persons': {
              'isReady': false,
              'isFailed': false,
              'progress': 0.001
            },
            'fingerPrints': {
              'isReady': false,
              'isFailed': false,
              'progress': 0.81238
            },
            'text': {
              'isReady': true,
              'isFailed': false,
              'progress': 1.0
            },
            'status': 'cancelled'
          }
        }, {
          'id': 'doc20',
          'metadata': {
            'title': 'Rumpelstielzchen',
            'author': 'Grimm',
            'publisher': 'Der Verlag',
            'publishYear': 1952,
            'genre': 'Maerchen',
            'edition': 'Standard'
          },
          'progress': {
            'graphView': {
              'isReady': false,
              'isFailed': false,
              'progress': 0.7123
            },
            'wordCloud': {
              'isReady': true,
              'isFailed': false,
              'progress': 1.0
            },
            'places': {
              'isReady': false,
              'isFailed': true,
              'progress': 0.6123
            },
            'persons': {
              'isReady': false,
              'isFailed': false,
              'progress': 0.001
            },
            'fingerPrints': {
              'isReady': false,
              'isFailed': false,
              'progress': 0.81238
            },
            'text': {
              'isReady': true,
              'isFailed': false,
              'progress': 1.0
            },
            'status': 'running'
          }
        }]
      },

      singleDocument: {
        'id': 'doc13a',
        'metadata': {
          'title': 'Rotkaeppchen',
          'author': 'Hans Mueller',
          'publisher': 'XY Verlag',
          'publishYear': 1957,
          'genre': 'Thriller',
          'edition': 'Limited Edition'
        },
        'metrics': {
          'characterCount': 19231,
          'chapterCount': 3,
          'personCount': 8,
          'placeCount': 2,
          'wordCount': 1999
        },
        'progress': {
          'graphView': {
            'isReady': false,
            'isFailed': false,
            'progress': 0.7123
          },
          'wordCloud': {
            'isReady': true,
            'isFailed': false,
            'progress': 1.0
          },
          'places': {
            'isReady': false,
            'isFailed': true,
            'progress': 0.6123
          },
          'persons': {
            'isReady': false,
            'isFailed': false,
            'progress': 0.001
          },
          'fingerPrints': {
            'isReady': false,
            'isFailed': false,
            'progress': 0.81238
          },
          'text': {
            'isReady': true,
            'isFailed': false,
            'progress': 1.0
          },
          'status': 'success'
        },
        'content': {
          'parts': [{
            'chapters': ['chapter1', 'chapter2', 'chapter3'],
            'number': 1,
            'title': 'Der erste Teil'
          }],
          'persons': ['person8Ben', 'person10Bert'],
          'places': ['place2Paris', 'place10Mordor']
        }
      },

      places: {
        'totalCount': 2,
        'places': [{
          'id': 'place10Paris',
          'displayName': 'Paris',
          'type': 'place',
          'rankingValue': 1,
          'frequency': 400,
          'entityRelations': []
        }, {
          'id': 'place6Hamburg',
          'displayName': 'Hamburg',
          'type': 'place',
          'rankingValue': 2,
          'frequency': 20,
          'entityRelations': []
        }]
      },

      singlePlace: {
        'id': 'place3Hamburg',
        'displayName': 'Hamburg',
        'type': 'place',
        'attributes': [{
          'id': 'attr12697',
          'content': '1700km2',
          'type': 'area'
        }, {
          'id': 'at65157',
          'content': 'Hamburg',
          'type': 'state'
        }],
        'rankingValue': 3,
        'frequency': 20,
        'entityRelations': [{
          'relatedEntity': 'person10Ben',
          'weight': 0.81234
        }, {
          'relatedEntity': 'person18Bert',
          'weight': 0.7345
        }]
      },

      parts: {
        'totalCount': '15',
        'parts': [{
          'number': 1,
          'title': 'Der erste Teil',
          'chapters': [{
            'id': '1-1',
            'title': 'Anfang',
            'number': 1,
            'length': 1000,
            'range': {
              'start': {
                'chapter': '1-1',
                'offset': 0,
                'progress': 0
              },
              'end': {
                'chapter': '1-1',
                'offset': 1000,
                'progress': 0.237261231
              },
              'length': 1000
            }
          }, {
            'id': '1-2',
            'title': 'Mittel',
            'number': 2,
            'length': 2000,
            'range': {
              'start': {
                'chapter': '1-2',
                'offset': 1000,
                'progress': 0.29
              },
              'end': {
                'chapter': '1-2',
                'offset': 3000,
                'progress': 0.487261231
              },
              'length': 2000
            }
          }, {
            'id': '1-3',
            'title': 'Ende',
            'number': 3,
            'length': 2000,
            'range': {
              'start': {
                'chapter': '1-3',
                'offset': 3000,
                'progress': 0.682381
              },
              'end': {
                'chapter': '1-3',
                'offset': 5000,
                'progress': 0.87261231
              },
              'length': 2000
            }
          }]
        }]
      },

      singleChapter: {
        'id': '1-1',
        'title': 'Anfang',
        'number': 13,
        'length': 1000,
        'text': 'When Mr. Bilbo Baggins of Bag End announced that he would shortly be celebrating his eleventy-first birthday with a party of special magnificence, there was much talk and excitement in Hobbiton.Bilbo was very rich and very peculiar, and had been the wonder of the Shire for sixty years, ever since his remarkable disappearance and unexpected return. The riches he had brought back from his travels had now become a local legend, and it was popularly believed, whatever the old folk might say, that the Hill at Bag End was full of tunnels stuffed with treasure. And if that was not enough for fame, there was also his prolonged vigour to marvel at. Time wore on, but it seemed to have little effect on Mr. Baggins. At ninety he was much the same as at fifty. At ninety-nine they began to call him well-preserved; but unchanged would have been nearer the mark. There were some that shook their heads and thought this was too much of a good thing; it seemed unfair that anyone should possess (apparently) perpetual youth as well as (reputedly) inexhaustible wealth.‘It will have to be paid for,’ they said. ‘It isn’t natural, and trouble will come of it!’',
        'range': {
          'start': {
            'chapter': '1-1',
            'offset': 0,
            'progress': 0.182381
          },
          'end': {
            'chapter': '1-1',
            'offset': 1000,
            'progress': 0.87261231
          },
          'length': 1000
        }
      },

      secondChapter: {
        'id': '1-13',
        'title': 'Seenot',
        'number': 13,
        'length': 2000,
        'text': '‘But what about this Frodo that lives with him?’ asked Old Noakes of Bywater. ‘Baggins is his name, but he’s more than half a Brandybuck, they say. It beats me why any Baggins of Hobbiton should go looking for a wife away there in Buckland, where folks are so queer.’',
        'range': {
          'start': {
            'chapter': '1-13',
            'offset': 1000,
            'progress': 0.182381
          },
          'end': {
            'chapter': '1-13',
            'offset': 3000,
            'progress': 0.87261231
          },
          'length': 2000
        }
      },

      thirdChapter: {
        'id': '1-13',
        'title': 'Ende',
        'number': 13,
        'length': 2000,
        'text': 'That very month was September, and as fine as you could ask. A day or two later a rumour (probably started by the knowledgeable Sam) was spread about that there were going to be fireworks – fireworks, what is more, such as had not been seen in the Shire for nigh on a century, not indeed since the Old Took died.Days passed and The Day drew nearer. An odd-looking waggon laden with odd-looking packages rolled into Hobbiton one evening and toiled up the Hill to Bag End. The startled hobbits peered out of lamplit doors to gape at it. It was driven by outlandish folk, singing strange songs: dwarves with long beards and deep hoods. A few of them remained at Bag End. At the end of the second week in September a cart came in through Bywater from the direction of Brandywine Bridge in broad daylight. An old man was driving it all alone. He wore a tall pointed blue hat, a long grey cloak, and a silver scarf. He had a long white beard and bushy eyebrows that stuck out beyond the brim of his hat. Small hobbit-children ran after the cart all through Hobbiton and right up the hill. It had a cargo of fireworks, as they rightly guessed. At Bilbo’s front door the old man began to unload: there were great bundles of fireworks of all sorts and shapes, each labelled with a large red G and the elf-rune, .That was Gandalf’s mark, of course, and the old man was Gandalf the Wizard, whose fame in the Shire was due mainly to his skill with fires, smokes, and lights. His real business was far more difficult and dangerous, but the Shire-folk knew nothing about it. To them he was just one of the ‘attractions’ at the Party. Hence the excitement of the hobbit-children. ‘G for Grand!’ they shouted, and the old man smiled. They knew him by sight, though he only appeared in Hobbiton occasionally and never stopped long; but neither they nor any but the oldest of their elders had seen one of his firework displays – they now belonged to a legendary past.',
        'range': {
          'start': {
            'chapter': '1-13',
            'offset': 3000,
            'progress': 0.182381
          },
          'end': {
            'chapter': '1-13',
            'offset': 5000,
            'progress': 0.87261231
          },
          'length': 2000
        }
      },

      fingerprint: {
        'occurrences': [{
          'start': {
            'chapter': 1.18,
            'offset': 13,
            'progress': 0
          },
          'end': {
            'chapter': 1.18,
            'offset': 29,
            'progress': 0.05
          },
          'length': 14123
        }, {
          'start': {
            'chapter': 1.18,
            'offset': 13,
            'progress': 0.05
          },
          'end': {
            'chapter': 1.18,
            'offset': 29,
            'progress': 0.1
          },
          'length': 14123
        }, {
          'start': {
            'chapter': 1.18,
            'offset': 13,
            'progress': 0.2
          },
          'end': {
            'chapter': 1.18,
            'offset': 29,
            'progress': 0.25
          },
          'length': 14123
        }, {
          'start': {
            'chapter': 1.18,
            'offset': 13,
            'progress': 0.3
          },
          'end': {
            'chapter': 1.18,
            'offset': 29,
            'progress': 0.35
          },
          'length': 14123
        }, {
          'start': {
            'chapter': 1.18,
            'offset': 13,
            'progress': 0.5
          },
          'end': {
            'chapter': 1.18,
            'offset': 29,
            'progress': 0.55
          },
          'length': 14123
        }, {
          'start': {
            'chapter': 1.18,
            'offset': 13,
            'progress': 0.65
          },
          'end': {
            'chapter': 1.18,
            'offset': 29,
            'progress': 0.7
          },
          'length': 14123
        }, {
          'start': {
            'chapter': 1.18,
            'offset': 13,
            'progress': 0.7
          },
          'end': {
            'chapter': 1.18,
            'offset': 29,
            'progress': 0.75
          },
          'length': 14123
        }, {
          'start': {
            'chapter': 1.18,
            'offset': 13,
            'progress': 0.8
          },
          'end': {
            'chapter': 1.18,
            'offset': 29,
            'progress': 0.85
          },
          'length': 14123
        }, {
          'start': {
            'chapter': 1.18,
            'offset': 13,
            'progress': 0.85
          },
          'end': {
            'chapter': 1.18,
            'offset': 29,
            'progress': 0.90
          },
          'length': 14123
        }, {
          'start': {
            'chapter': 1.18,
            'offset': 13,
            'progress': 0.95
          },
          'end': {
            'chapter': 1.18,
            'offset': 29,
            'progress': 1
          },
          'length': 14123
        }]
      },

      relationOccurrences: {
        'occurrences': [{
          'start': {
            'chapter': '1-1',
            'offset': 5,
            'progress': 0
          },
          'end': {
            'chapter': '1-1',
            'offset': 22,
            'progress': 0.05
          },
          'length': 25
        }, {
          'start': {
            'chapter': '1-1',
            'offset': 194,
            'progress': 0.05
          },
          'end': {
            'chapter': '1-1',
            'offset': 199,
            'progress': 0.1
          },
          'length': 15
        }, {
          'start': {
            'chapter': '1-1',
            'offset': 701,
            'progress': 0.05
          },
          'end': {
            'chapter': '1-1',
            'offset': 712,
            'progress': 0.1
          },
          'length': 15
        }, {
          'start': {
            'chapter': '1-2',
            'offset': 1000,
            'progress': 0.2
          },
          'end': {
            'chapter': '1-2',
            'offset': 1077,
            'progress': 0.25
          },
          'length': 30
        }, {
          'start': {
            'chapter': '1-3',
            'offset': 3100,
            'progress': 0.3
          },
          'end': {
            'chapter': '1-3',
            'offset': 3150,
            'progress': 0.35
          },
          'length': 50
        }, {
          'start': {
            'chapter': '1-3',
            'offset': 4359,
            'progress': 0.3
          },
          'end': {
            'chapter': '1-3',
            'offset': 4377,
            'progress': 0.35
          },
          'length': 20
        }]
      },

      singlePerson: {
        'id': 'person8Hugo',
        'displayName': 'Hugo',
        'type': 'person',
        'attributes': [{
          'id': 'attr12397',
          'content': '15',
          'attributetype': 'age'
        }, {
          'id': 'at65657',
          'content': 'Hugo Martin',
          'attributetype': 'name'
        }],
        'rankingValue': 3,
        'frequency': 301,
        'entityRelations': [{
          'relatedEntity': 'person10Ben',
          'weight': 0.81234
        }, {
          'relatedEntity': 'person18Bert',
          'weight': 0.7345
        }]
      },

      persons: {
        'totalCount': 2,
        'persons': [{
          'id': 'person8Hugo',
          'displayName': 'Hugo',
          'type': 'person',
          'rankingValue': 3,
          'frequency': 301,
          'entityRelations': []
        }, {
          'id': 'person10Bert',
          'displayName': 'Bert',
          'type': 'person',
          'rankingValue': 7,
          'frequency': 153,
          'entityRelations': []
        }]
      },

      entities: [{
        'id': 'bilbo',
        'displayName': 'Mr. Bilbo Baggins',
        'type': 'person',
        'attributes': [{
          'id': 'attr12397',
          'content': '15',
          'type': 'age'
        }, {
          'id': 'at65657',
          'content': 'Mr. Baggins',
          'attributetype': 'name'
        }, {
          'id': 'at65657',
          'content': 'Bilbo',
          'attributetype': 'name'
        }],
        'rankingValue': 3,
        'frequency': 914,
        'entityRelations': [{
          'relatedEntity': 'person10Ben',
          'weight': 0.81234
        }, {
          'relatedEntity': 'person18Bert',
          'weight': 0.7345
        }]
      }, {
        'id': 'person9',
        'displayName': 'Frodo',
        'type': 'person',
        'attributes': [],
        'rankingValue': 2,
        'frequency': 1052,
        'entityRelations': [{
          'relatedEntity': 'person10Ben',
          'weight': 0.81234
        }, {
          'relatedEntity': 'person18Bert',
          'weight': 0.7345
        }]
      }, {
        'id': 'person9',
        'displayName': 'Gandalf',
        'type': 'person',
        'attributes': [{
          'id': 'attr12397',
          'content': 'Gandalf the Wizard',
          'attributetype': 'name'
        }],
        'rankingValue': 7,
        'frequency': 321,
        'entityRelations': [{
          'relatedEntity': 'person10Ben',
          'weight': 0.81234
        }, {
          'relatedEntity': 'person18Bert',
          'weight': 0.7345
        }]
      }, {
        'id': 'person9',
        'displayName': 'Old Noakes of Bywater',
        'type': 'person',
        'attributes': [],
        'rankingValue': 70,
        'frequency': 65,
        'entityRelations': [{
          'relatedEntity': 'person10Ben',
          'weight': 0.81234
        }, {
          'relatedEntity': 'person18Bert',
          'weight': 0.7345
        }]
      }],

      graphNetworkEntities: [{
        'id': 'person8Andi',
        'displayName': 'Andi',
        'type': 'person',
        'attributes': [{
          'id': 'attr12397',
          'content': '15',
          'attributetype': 'age'
        }, {
          'id': 'at65657',
          'content': 'Andreas',
          'attributetype': 'name'
        }],
        'rankingValue': 3,
        'frequency': 300,
        'entityRelations': [{
          'relatedEntity': 'person10Bert',
          'weight': 0.81234
        }, {
          'relatedEntity': 'person18Ben',
          'weight': 0.7345
        }]
      }, {
        'id': 'person10Bert',
        'displayName': 'Bert',
        'type': 'person',
        'attributes': [{
          'id': 'attr1041',
          'content': '22',
          'attributetype': 'age'
        }, {
          'id': 'at7214',
          'content': 'Bert Bauer',
          'attributetype': 'name'
        }],
        'rankingValue': 2,
        'frequency': 59,
        'entityRelations': [{
          'relatedEntity': 'person8Hugo',
          'weight': 0.222
        }]
      }, {
        'id': 'person61Nobody',
        'displayName': 'Nobody',
        'type': 'person',
        'attributes': [{
          'id': 'at542',
          'content': 'Nobo Dy',
          'attributetype': 'name'
        }],
        'rankingValue': 25,
        'frequency': 192,
        'entityRelations': []
      }],

      entityRelations: {
        'entityIds': ['person8Andi', 'person10Bert', 'person61Nobody'],
        'relations': [{
          'entityAId': 'person8Andi',
          'entityBId': 'person10Bert',
          'weight': 0.81234
        }]
      },

      wordcloud: {
        'items': [{
          'word': 'Ring',
          'frequency': 95
        }, {
          'word': 'Time',
          'frequency': 44
        }, {
          'word': 'Sword',
          'frequency': 22
        }, {
          'word': 'Darkness',
          'frequency': 11
        }, {
          'word': 'River',
          'frequency': 5
        }, {
          'word': 'Fear',
          'frequency': 10
        }, {
          'word': 'Mountains',
          'frequency': 17
        }, {
          'word': 'Company',
          'frequency': 34
        }, {
          'word': 'Hands',
          'frequency': 66
        }, {
          'word': 'Together',
          'frequency': 23
        }, {
          'word': 'Beyond',
          'frequency': 5
        }, {
          'word': 'Road',
          'frequency': 82
        }, {
          'word': 'Words',
          'frequency': 72
        }, {
          'word': 'Night',
          'frequency': 49
        }, {
          'word': 'End',
          'frequency': 60
        }, {
          'word': 'Against',
          'frequency': 19
        }, {
          'word': 'Door',
          'frequency': 37
        }, {
          'word': 'Upon',
          'frequency': 61
        }, {
          'word': 'Fire',
          'frequency': 86
        }, {
          'word': 'Folk',
          'frequency': 73
        }, {
          'word': 'Soon',
          'frequency': 71
        }, {
          'word': 'Suddenly',
          'frequency': 19
        }, {
          'word': 'Voice',
          'frequency': 6
        }, {
          'word': 'Moment',
          'frequency': 3
        }, {
          'word': 'Above',
          'frequency': 64
        }, {
          'word': 'Gate',
          'frequency': 68
        }, {
          'word': 'Looking',
          'frequency': 16
        }, {
          'word': 'Indeed',
          'frequency': 42
        }, {
          'word': 'North',
          'frequency': 28
        }, {
          'word': 'Ran',
          'frequency': 47
        }, {
          'word': 'Clear',
          'frequency': 28
        }, {
          'word': 'Riders',
          'frequency': 2
        }, {
          'word': 'Since',
          'frequency': 64
        }, {
          'word': 'Gone',
          'frequency': 23
        }, {
          'word': 'Light',
          'frequency': 33
        }, {
          'word': 'Away',
          'frequency': 53
        }, {
          'word': 'About',
          'frequency': 17
        }, {
          'word': 'Behind',
          'frequency': 41
        }, {
          'word': 'Back',
          'frequency': 49
        }, {
          'word': 'Go',
          'frequency': 63
        }]
      },

      wordcloudhugo: {
        'items': [{
          'word': 'Hugo',
          'frequency': 42
        }, {
          'word': 'Test',
          'frequency': 23
        }]
      },

      wordcloudbert: {
        'items': [{
          'word': 'Bert',
          'frequency': 42
        }, {
          'word': 'Word',
          'frequency': 23
        }]
      },

      analysisProgress: {
        'graphView': {
          'isReady': false,
          'isFailed': false,
          'progress': 0.7123
        },
        'wordCloud': {
          'isReady': true,
          'isFailed': false,
          'progress': 1.0
        },
        'places': {
          'isReady': false,
          'isFailed': true,
          'progress': 0.6123
        },
        'persons': {
          'isReady': false,
          'isFailed': false,
          'progress': 0.001
        },
        'fingerPrints': {
          'isReady': false,
          'isFailed': false,
          'progress': 0.81238
        },
        'text': {
          'isReady': true,
          'isFailed': false,
          'progress': 1.0
        },
        'status': 'success'
      },

      search: {
        'occurrences': [{
          'start': {
            'chapter': '1.18',
            'offset': 13,
            'progress': 0.182381
          },
          'end': {
            'chapter': '1.18',
            'offset': 29,
            'progress': 0.87261231
          },
          'length': 14123
        }, {
          'start': {
            'chapter': '2.23',
            'offset': 12,
            'progress': 0.7351
          },
          'end': {
            'chapter': '2.24',
            'offset': 18,
            'progress': 0.812231
          },
          'length': 989
        }]
      },

      analysisParameters: {
        'totalCount': 3,
        'parameters': [{
          'name': 'relationTimeStepCount',
          'label': 'Relation time steps',
          'attributeType': 'int',
          'description': 'The number of steps between the relations over time.',
          'defaultValue': 100,
          'min': 1,
          'max': 1000
        }, {
          'name': 'wordCloudItemsCount',
          'label': 'Word cloud items',
          'attributeType': 'int',
          'description': 'The number of items visualized in the word cloud.',
          'defaultValue': 100,
          'min': 10,
          'max': 100
        }, {
          'name': 'stopWordListEnabled',
          'label': 'Enable stop word',
          'attributeType': 'boolean',
          'description': 'If the stop word list should be used to filter probalby unnecessary words.',
          'defaultValue': true
        }, {
          'name': 'stopWordListEnabled',
          'label': 'Enable stop word',
          'attributeType': 'boolean',
          'description': 'If the stop word list should be used to filter probalby unnecessary words.',
          'defaultValue': true
        }, {
          "name": "documentLanguage",
          "label": "Document language",
          "attributeType": "enum",
          "description": "The language of the imported document",
          "values": [
            {"name": "german", "label": "German"},
            {"name": "english", "label": "English"}
          ],
          "defaultValue": "english"
        }, {
          "name": "stopWordList",
          "label": "Stop word list",
          "attributeType": "string",
          "description": "A list of stop words for the Word Cloud",
          "defaultValue": "hi"
        }]
      },

      plotviewData: {
        "characters": [
          {
            "name": "Elizabeth",
            "id": "62994bcc-d26c-4dde-8513-dc4085aaf58d",
            "group": 0
          }, {
            "name": "Felix",
            "id": "4b8bea39-42a2-41db-9d55-9496b616856e",
            "group": 1
          }, {
            "name": "Justine",
            "id": "f55ed527-ccaf-4770-9fb8-917f1fad1312",
            "group": 2
          }, {
            "name": "William",
            "id": "1a73c023-0981-4d1d-81da-01d9607cba38",
            "group": 3
          }, {
            "name": "Justine",
            "id": "37a83c51-f57c-49e6-a260-f18a1f5bc551",
            "group": 4
          }, {
            "name": "Clerval",
            "id": "2a7edd22-e6ac-4206-b08a-e5c78851fc0d",
            "group": 5
          }, {
            "name": "Agatha",
            "id": "2659a715-aca5-4f85-bbb8-9ac6d9c61a34",
            "group": 6
          }, {
            "name": "Henry",
            "id": "3402dc17-5bbc-46be-8347-6a4bf1c85f1f",
            "group": 7
          }, {
            "name": "rain",
            "id": "fd14d83b-ac15-4937-a463-f82b6dd8b7d3",
            "group": 8
          }, {"name": "Mr. Kirwin", "id": "688adf71-7543-40c1-b675-420284fb2172", "group": 9}],
        "panels": 28,
        "scenes": [
          {
            "start": 0,
            "duration": 1,
            "id": 0,
            "chars": [],
            "title": "1 - Letter 1"
          }, {"start": 1, "duration": 1, "id": 1, "chars": [], "title": "2 - Letter 2"}, {
            "start": 2,
            "duration": 1,
            "id": 2,
            "chars": [],
            "title": "3 - Letter 3"
          }, {"start": 3, "duration": 1, "id": 3, "chars": [], "title": "4 - Letter 4"}, {
            "start": 4,
            "duration": 1,
            "id": 4,
            "chars": [],
            "title": "5 - Chapter 1"
          }, {
            "start": 5,
            "duration": 1,
            "id": 5,
            "chars": ["2a7edd22-e6ac-4206-b08a-e5c78851fc0d", "62994bcc-d26c-4dde-8513-dc4085aaf58d"],
            "title": "6 - Chapter 2"
          }, {
            "start": 6,
            "duration": 1,
            "id": 6,
            "chars": ["62994bcc-d26c-4dde-8513-dc4085aaf58d", "3402dc17-5bbc-46be-8347-6a4bf1c85f1f"],
            "title": "7 - Chapter 3"
          }, {"start": 7, "duration": 1, "id": 7, "chars": [], "title": "8 - Chapter 4"}, {
            "start": 8,
            "duration": 1,
            "id": 8,
            "chars": ["fd14d83b-ac15-4937-a463-f82b6dd8b7d3", "2a7edd22-e6ac-4206-b08a-e5c78851fc0d", "62994bcc-d26c-4dde-8513-dc4085aaf58d"],
            "title": "9 - Chapter 5"
          }, {
            "start": 9,
            "duration": 1,
            "id": 9,
            "chars": ["37a83c51-f57c-49e6-a260-f18a1f5bc551", "f55ed527-ccaf-4770-9fb8-917f1fad1312", "62994bcc-d26c-4dde-8513-dc4085aaf58d", "3402dc17-5bbc-46be-8347-6a4bf1c85f1f"],
            "title": "10 - Chapter 6"
          }, {
            "start": 10,
            "duration": 1,
            "id": 10,
            "chars": ["1a73c023-0981-4d1d-81da-01d9607cba38", "fd14d83b-ac15-4937-a463-f82b6dd8b7d3", "37a83c51-f57c-49e6-a260-f18a1f5bc551", "62994bcc-d26c-4dde-8513-dc4085aaf58d", "3402dc17-5bbc-46be-8347-6a4bf1c85f1f"],
            "title": "11 - Chapter 7"
          }, {
            "start": 11,
            "duration": 1,
            "id": 11,
            "chars": ["1a73c023-0981-4d1d-81da-01d9607cba38", "f55ed527-ccaf-4770-9fb8-917f1fad1312", "62994bcc-d26c-4dde-8513-dc4085aaf58d"],
            "title": "12 - Chapter 8"
          }, {
            "start": 12,
            "duration": 1,
            "id": 12,
            "chars": ["1a73c023-0981-4d1d-81da-01d9607cba38", "f55ed527-ccaf-4770-9fb8-917f1fad1312", "62994bcc-d26c-4dde-8513-dc4085aaf58d"],
            "title": "13 - Chapter 9"
          }, {
            "start": 13,
            "duration": 1,
            "id": 13,
            "chars": ["fd14d83b-ac15-4937-a463-f82b6dd8b7d3"],
            "title": "14 - Chapter 10"
          }, {
            "start": 14,
            "duration": 1,
            "id": 14,
            "chars": ["fd14d83b-ac15-4937-a463-f82b6dd8b7d3"],
            "title": "15 - Chapter 11"
          }, {
            "start": 15,
            "duration": 1,
            "id": 15,
            "chars": ["2659a715-aca5-4f85-bbb8-9ac6d9c61a34", "4b8bea39-42a2-41db-9d55-9496b616856e"],
            "title": "16 - Chapter 12"
          }, {
            "start": 16,
            "duration": 1,
            "id": 16,
            "chars": ["2659a715-aca5-4f85-bbb8-9ac6d9c61a34", "4b8bea39-42a2-41db-9d55-9496b616856e"],
            "title": "17 - Chapter 13"
          }, {
            "start": 17,
            "duration": 1,
            "id": 17,
            "chars": ["2659a715-aca5-4f85-bbb8-9ac6d9c61a34", "4b8bea39-42a2-41db-9d55-9496b616856e"],
            "title": "18 - Chapter 14"
          }, {
            "start": 18,
            "duration": 1,
            "id": 18,
            "chars": ["2659a715-aca5-4f85-bbb8-9ac6d9c61a34", "4b8bea39-42a2-41db-9d55-9496b616856e"],
            "title": "19 - Chapter 15"
          }, {
            "start": 19,
            "duration": 1,
            "id": 19,
            "chars": ["4b8bea39-42a2-41db-9d55-9496b616856e"],
            "title": "20 - Chapter 16"
          }, {
            "start": 20,
            "duration": 1,
            "id": 20,
            "chars": [],
            "title": "21 - Chapter 17"
          }, {
            "start": 21,
            "duration": 1,
            "id": 21,
            "chars": ["62994bcc-d26c-4dde-8513-dc4085aaf58d", "3402dc17-5bbc-46be-8347-6a4bf1c85f1f"],
            "title": "22 - Chapter 18"
          }, {
            "start": 22,
            "duration": 1,
            "id": 22,
            "chars": ["3402dc17-5bbc-46be-8347-6a4bf1c85f1f"],
            "title": "23 - Chapter 19"
          }, {
            "start": 23,
            "duration": 1,
            "id": 23,
            "chars": ["62994bcc-d26c-4dde-8513-dc4085aaf58d", "688adf71-7543-40c1-b675-420284fb2172"],
            "title": "24 - Chapter 20"
          }, {
            "start": 24,
            "duration": 1,
            "id": 24,
            "chars": ["1a73c023-0981-4d1d-81da-01d9607cba38", "37a83c51-f57c-49e6-a260-f18a1f5bc551", "2a7edd22-e6ac-4206-b08a-e5c78851fc0d", "688adf71-7543-40c1-b675-420284fb2172"],
            "title": "25 - Chapter 21"
          }, {
            "start": 25,
            "duration": 1,
            "id": 25,
            "chars": ["37a83c51-f57c-49e6-a260-f18a1f5bc551", "62994bcc-d26c-4dde-8513-dc4085aaf58d"],
            "title": "26 - Chapter 22"
          }, {
            "start": 26,
            "duration": 1,
            "id": 26,
            "chars": ["fd14d83b-ac15-4937-a463-f82b6dd8b7d3", "62994bcc-d26c-4dde-8513-dc4085aaf58d"],
            "title": "27 - Chapter 23"
          }, {
            "start": 27,
            "duration": 1,
            "id": 27,
            "chars": ["1a73c023-0981-4d1d-81da-01d9607cba38", "4b8bea39-42a2-41db-9d55-9496b616856e", "62994bcc-d26c-4dde-8513-dc4085aaf58d"],
            "title": "28 - Chapter 24"
          }]
      }
    };

  });
})(angular);
