(function(angular) {
  'use strict';

  var vitaDirectives = angular.module('vitaDirectives');

  vitaDirectives.directive('documentPreview', ['DocumentParts', 'ChapterText',
      function(DocumentParts, ChapterText) {

        var lastPreviewedDocumentId;

        function link(scope) {
          scope.$watch('document', function(newValue) {
            if (!newValue) {
              return;
            }

            scope.documentTitle = scope.document.metadata.title;

            var documentId = scope.document.id;
            if (lastPreviewedDocumentId === documentId) {
              // Don't load the same text multiple times
              return;
            }

            DocumentParts.get({
              documentId: documentId
            }, function(structure) {
              previewFirstChapter(scope, structure.parts);
              lastPreviewedDocumentId = documentId;
            });
          });
        }

        function previewFirstChapter(scope, parts) {
          scope.previewText = undefined;
          if (parts.length === 0 || parts[0].chapters.length === 0) {
            scope.previewText = 'Currently no text available';
            return;
          }

          ChapterText.get({
            documentId: scope.document.id,
            chapterId: parts[0].chapters[0].id
          }, function(chapter) {
            scope.previewText = chapter.text;
          });
        }

        return {
          restrict: 'A',
          scope: {
            document: '='
          },
          link: link,
          templateUrl: 'templates/documentpreview.html'
        };
      }]);

})(angular);
