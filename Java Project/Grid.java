import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class Grid<T> {
    private int rows;
    private int columns;
    private List<List<T>> cells;

    public Grid(int rows, int columns) {
        if (rows <= 0 || columns <= 0) {
            throw new IllegalArgumentException(
                "Rows and columns must be greater than zero"
            );
        }

        this.rows = rows;
        this.columns = columns;
        this.cells = new ArrayList<List<T>>();

        // Create each row
        for (int row = 0; row < rows; row++) {
            List<T> currentRow = new ArrayList<T>();

            // create columns in rows
            for (int column = 0; column < columns; column++) {
                currentRow.add(null);
            }

            cells.add(currentRow);
        }
    }

    public int getRows() {
        return rows;
    }

    public int getColumns() {
        return columns;
    }

    public T getCell(int row, int column) {
        if (!isInBounds(row, column)) {
            throw new IndexOutOfBoundsException(
                "Invalid grid position: ("
                    + row + ", " + column + ")"
            );
        }

        return cells.get(row).get(column);
    }

    public void setCell(int row, int column, T value) {
        if (!isInBounds(row, column)) {
            throw new IndexOutOfBoundsException(
                "Invalid grid position: ("
                    + row + ", " + column + ")"
            );
        }

        cells.get(row).set(column, value);
    }

    public void fill(T value) {
        cells.forEach(currentRow -> Collections.fill(currentRow, value));
    }

    public boolean isInBounds(int row, int column) {
        return row >= 0 && row < rows && column >= 0 && column < columns;
    }

    public List<T> neighbours(int row, int column) {
        return neighbourPositions(row, column)
                .stream()
                .map(position -> getCell(position.getRow(), position.getColumn()))
                .collect(Collectors.toList());
    }

    public List<Position> neighbourPositions(int row, int column) {
        if (!isInBounds(row, column)) {
            throw new IndexOutOfBoundsException(
                "Invalid grid position: ("
                    + row + ", " + column + ")"
            );
        }

        List<Position> positions = new ArrayList<Position>();

        for (int rowOffset = -1; rowOffset <= 1; rowOffset++) {
            for (int columnOffset = -1; columnOffset <= 1; columnOffset++) {

                if (rowOffset == 0 && columnOffset == 0) {
                    continue;
                }

                int neighbourRow = row + rowOffset;
                int neighbourColumn = column + columnOffset;

                if (isInBounds(neighbourRow, neighbourColumn)) {
                    positions.add(new Position(neighbourRow, neighbourColumn));
                }
            }
        }
        return positions;
    }

    public GridIterator<Entry<T>> createIterator() {
        return new RowByRowIterator();
    }

    public GridIterator<Entry<T>> createIterator(Predicate<T> filter) {
        return new FilteredIterator(filter);
    }

    public Stream<Entry<T>> stream() {
        GridIterator<Entry<T>> source = createIterator();

        Iterator<Entry<T>> adapter = new Iterator<Entry<T>>() {
            @Override
            public boolean hasNext() {
                return source.hasNext();
            }

            @Override
            public Entry<T> next() {
                return source.next();
            }
        };

        return StreamSupport.stream(
                Spliterators.spliteratorUnknownSize(adapter, Spliterator.ORDERED), false);
    }

    private class RowByRowIterator implements GridIterator<Entry<T>> {
        private int nextRow = 0;
        private int nextColumn = 0;

        @Override
        public boolean hasNext() {
            return nextRow < rows;
        }

        @Override
        public Entry<T> next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            Entry<T> entry = new Entry<T>(nextRow, nextColumn, getCell(nextRow, nextColumn));

            nextColumn++;

            if (nextColumn >= columns) {
                nextColumn = 0;
                nextRow++;
            }

            return entry;
        }
    }

    private class FilteredIterator implements GridIterator<Entry<T>> {
        private final GridIterator<Entry<T>> source = new RowByRowIterator();
        private final Predicate<T> filter;
        private Entry<T> upcoming;

        FilteredIterator(Predicate<T> filter) {
            this.filter = filter;
        }

        @Override
        public boolean hasNext() {
            while (upcoming == null && source.hasNext()) {
                Entry<T> candidate = source.next();

                if (filter.test(candidate.getValue())) {
                    upcoming = candidate;
                }
            }

            return upcoming != null;
        }

        @Override
        public Entry<T> next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }

            Entry<T> result = upcoming;
            upcoming = null;

            return result;
        }
    }

    public static class Entry<T> {
        private int row;
        private int column;
        private T value;

        public Entry(int row, int column, T value) {
            this.row = row;
            this.column = column;
            this.value = value;
        }

        public int getRow() {
            return row;
        }

        public int getColumn() {
            return column;
        }

        public T getValue() {
            return value;
        }
    }

    public static class Position {
        private int row;
        private int column;

        public Position(int row, int column) {
            this.row = row;
            this.column = column;
        }

        public int getRow() {
            return row;
        }

        public int getColumn() {
            return column;
        }
    }
}
