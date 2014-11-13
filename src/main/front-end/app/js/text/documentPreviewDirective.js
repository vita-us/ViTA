(function(angular) {
  'use strict';

  var vitaDirectives = angular.module('vitaDirectives');

  vitaDirectives.directive('documentPreview', ['DocumentParts', 'ChapterText',
      function(DocumentParts, ChapterText) {

        function link(scope) {
          scope.$watch('document', function(newValue) {
            if (!newValue) {
              return;
            }

            DocumentParts.get({
              documentId: scope.document.id
            }, function(structure) {
              previewFirstChapter(scope, structure.parts);
            });
          });
        }

        function previewFirstChapter(scope, parts) {
          if (parts.length === 0 || parts[0].chapters.length === 0) {
            scope.previewText = 'Currently no text available';
            return;
          }

          ChapterText.get({
            documentId: scope.document.id,
            chapterId: parts[0].chapters[0].id
          }, function(chapter) {
            scope.previewText = chapter.text;
            scope.documentTitle = scope.document.metadata.title;
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
