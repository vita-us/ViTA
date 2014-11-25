(function(angular) {
  'use strict';

  var vitaDirectives = angular.module('vitaDirectives');

  vitaDirectives.directive('chapter', ['ChapterText', function(ChapterText) {
    function link(scope) {
      ChapterText.get({
        documentId: scope.documentId,
        chapterId: scope.chapterData.id
      }, function(chapter) {
        scope.title = chapter.title || 'Chapter ' + chapter.number;
        scope.text = chapter.text;
      });
    }

    return {
      restrict: 'A',
      scope: {
        chapterData: '=',
        documentId: '='
      },
      templateUrl: 'templates/chapter.html',
      link: link
    };
  }]);

})(angular);
