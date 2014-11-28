package de.unistuttgart.vis.vita.model.document;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TextSpanIntersector {
  private enum EventType { ENTER, LEAVE };
  private static class Event implements Comparable<Event> {
    TextPosition pos;
    EventType type;

    Event(TextPosition pos, EventType type) {
      this.pos = pos;
      this.type = type;
    }

    @Override
    public int compareTo(Event o) {
      return pos.compareTo(o.pos);
    }

    @Override
    public String toString() {
      return String.format("%s@%s", type, pos);
    }
  }

  /**
   * Intersects multiple text span lists
   *
   * @param list of span lists. The spans in each list must not be overlapping
   * @return the text spans that are included in all given lists of text spans
   */
  public static List<TextSpan> intersect(List<List<TextSpan>> lists) {
    List<Event> events = new ArrayList<TextSpanIntersector.Event>();
    for (List<TextSpan> list : lists) {
      for (TextSpan span : list) {
        events.add(new Event(span.getStart(), EventType.ENTER));
        events.add(new Event(span.getEnd(), EventType.LEAVE));
      }
    }
    Collections.sort(events);
    TextPosition currentStart = null;
    List<TextSpan> result = new ArrayList<>();
    int currentCount = 0;
    for (Event event: events) {
      switch (event.type) {
        case ENTER:
          currentCount++;
          break;
        case LEAVE:
          currentCount--;
          break;
      }
      assert currentCount <= lists.size();
      if (currentCount == lists.size()) {
        currentStart = event.pos;
      } else if (currentStart != null) {
        result.add(new TextSpan(currentStart, event.pos));
        currentStart = null;
      }
    }
    return result;
  }
}
