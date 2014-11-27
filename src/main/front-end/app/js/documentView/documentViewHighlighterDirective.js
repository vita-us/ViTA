(function(angular) {
  'use strict';

  var vitaDirectives = angular.module('vitaDirectives');

  vitaDirectives.directive('documentViewHighlighter', [
      'ChapterText',
      'CssClass',
      function(ChapterText, CssClass) {

        var highlighterElement;

        function link(scope, element) {
          highlighterElement = element;
          scope.$watch('[occurrences, entities]', function(newValues, oldValues) {
            if (!angular.equals(newValues, oldValues)) {
              clearChapters();
              highlight(scope.occurrences, scope.documentId, scope.entities);
            }
          }, true);
          scope.$watch('selectedOccurrenceIndex', function(newValue, oldValue) {
            if (!angular.equals(newValue, oldValue) && !angular.isUndefined(newValue)) {
              highlightSelectedOccurrence(scope.selectedOccurrenceIndex);
            }
          }, true);
        }

        function highlight(occurrences, documentId, entities) {
          occurrences = angular.isUndefined(occurrences) ? [] : occurrences;

          occurrences = occurrences.sort(function(a, b) {
            return a.start.offset - b.start.offset;
          });

          occurrences.forEach(function(occurrence, i) {
            occurrence.index = i;
          });

          var chapterOccurrences = getOccurrencesByChapterId(occurrences);

          // Highlight each chapter that contains occurrence(s)
          Object.keys(chapterOccurrences).forEach(function(chapterId, i) {
            ChapterText.get({
              documentId: documentId,
              chapterId: chapterId
            }, function(chapter) {
              var chapterOffset = chapter.range.start.offset;
              highlightChapter(chapterOccurrences[chapterId], chapterOffset, chapterId, entities);
              if (i === 0) {
                highlightSelectedOccurrence(0);
              }
            });
          });
        }

        function highlightChapter(chapterOccurrences, chapterOffset, chapterId, entities) {
          var chapterHTMLId = 'chapter-' + chapterId;
          var $chapter = $(highlighterElement[0]).find('[id="' + chapterHTMLId + '"] p');
          if ($chapter.length === 0) {
            return;
          }
          var chapterText = $chapter.text();

          var splitParts = splitChapter(chapterText, chapterOccurrences, chapterOffset);

          var highlightedOccurrenceParts = addHighlights(splitParts.occurrenceParts,
                  chapterOccurrences, entities);

          var highlightedChapterText = mergeChapter(highlightedOccurrenceParts,
                  splitParts.nonOccurrenceParts);

          $chapter.html(highlightedChapterText);
        }

        function splitChapter(chapterText, chapterOccurrences, chapterOffset) {
          var occurrenceParts = [];
          var nonOccurrenceParts = [];

          nonOccurrenceParts.push(chapterText.slice(0, chapterOccurrences[0].start.offset
                  - chapterOffset));

          chapterOccurrences.forEach(function(singleOccurrence, i) {
            var occurrenceStart = singleOccurrence.start.offset;
            var occurrenceEnd = singleOccurrence.end.offset;
            var occurrenceText = chapterText.slice(occurrenceStart - chapterOffset, occurrenceEnd
                    - chapterOffset);
            occurrenceParts.push(occurrenceText);
            if (i !== chapterOccurrences.length - 1) {
              // Parts between two occurrences, does not exist for last
              // occurrence in a chapter
              nonOccurrenceParts.push(chapterText.slice(occurrenceEnd - chapterOffset,
                      chapterOccurrences[i + 1].start.offset - chapterOffset));
            } else {
              nonOccurrenceParts.push(chapterText.slice(occurrenceEnd - chapterOffset));
            }
          });

          return {
            occurrenceParts: occurrenceParts,
            nonOccurrenceParts: nonOccurrenceParts
          };
        }

        function addHighlights(occurrenceParts, occurrences, entities) {
          var highlightedOccurrenceParts = [];
          occurrenceParts.forEach(function(occurrencePart, i) {
            var highlightedOccurrencePart = wrap(highlightEntities(occurrencePart, entities),
                    'occurrence', 'occurrence-' + occurrences[i].index);
            highlightedOccurrenceParts.push(highlightedOccurrencePart);
          });
          return highlightedOccurrenceParts;
        }

        function highlightEntities(occurrenceText, entities) {
          if (angular.isUndefined(entities)) {
            return occurrenceText;
          }
          var highlightedText = occurrenceText;
          entities.forEach(function(entity) {
            var names = getAllNames(entity);

            // We need to highlight the longest names first
            names = names.sort(function(a, b) {
              return b.length - a.length;
            });

            names.forEach(function(name) {
              // dots in names must be escaped as they are a special character in a regex
              name = name.replace('.', '\\.');
              highlightedText = highlightedText.replace(new RegExp('(' + name + ')\\b', 'g'), wrap(
                      '$1', CssClass.forRankingValue(entity.rankingValue)));
            });
          });
          return highlightedText;
        }

        function mergeChapter(occurrenceParts, nonOccurrenceParts) {
          var mergedText = '';
          for (var i = 0; i < occurrenceParts.length; i++) {
            mergedText += nonOccurrenceParts[i] + occurrenceParts[i];
          }
          mergedText += nonOccurrenceParts[nonOccurrenceParts.length - 1];
          return mergedText;
        }

        function wrap(text, cssClass, id) {
          return '<span class="' + cssClass + '" id="' + id + '">' + text + '</span>';
        }

        function getAllNames(entity) {
          var names = [];
          names.push(entity.displayName);

          entity.attributes.forEach(function(attribute) {
            if (attribute.type === 'name') {
              names.push(attribute.content);
            }
          });
          return names;
        }

        function highlightSelectedOccurrence(selectedOccurrenceIndex) {
          var prevSelectedOccurence = $(highlighterElement[0]).find('.selected');
          if (prevSelectedOccurence.length !== 0) {
            prevSelectedOccurence.removeClass('selected');
          }
          var newSelectedOccurrence = $('#occurrence-' + selectedOccurrenceIndex);
          if (newSelectedOccurrence.length === 0) {
            return;
          }
          newSelectedOccurrence.addClass('selected');
          newSelectedOccurrence[0].scrollIntoView();
        }

        function getOccurrencesByChapterId(occurrences) {
          var chapterOccurrences = {};

          occurrences.forEach(function(occurrence) {
            var chapterId = occurrence.start.chapter;

            if (angular.isUndefined(chapterOccurrences[chapterId])) {
              chapterOccurrences[chapterId] = [];
            }
            chapterOccurrences[chapterId].push(occurrence);
          });

          return chapterOccurrences;
        }

        function clearChapters() {
          $(highlighterElement[0]).find('[id^="chapter-"] p').each(function() {
            $(this).html($(this).text());
          });
        }

        return {
          restrict: 'A',
          scope: {
            occurrences: '=',
            documentId: '=',
            entities: '=',
            selectedOccurrenceIndex: '='
          },
          link: link
        };
      }]);

})(angular);
