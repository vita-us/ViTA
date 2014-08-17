(function(angular) {
  'use strict';

  var app = angular.module('vita');

  app.run(function($httpBackend) {

    var documents = {
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
    };

    var singleDocument = {
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
    };

    $httpBackend.whenGET(new RegExp('\.html$')).passThrough();
    $httpBackend.whenGET(new RegExp('/documents$')).respond(documents);
    $httpBackend.whenGET(new RegExp('/documents/[^/]+$')).respond(singleDocument);

  });
})(angular);
