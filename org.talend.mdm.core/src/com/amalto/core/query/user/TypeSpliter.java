package com.amalto.core.query.user;

import java.util.*;

import org.talend.mdm.commmon.metadata.ComplexTypeMetadata;

import com.amalto.core.storage.Storage;
import com.amalto.core.storage.StorageResults;
import com.amalto.core.storage.record.DataRecord;

public class TypeSpliter {

    public static StorageResults split(Select select, Storage storage) {
        if (storage == null) {
            throw new IllegalArgumentException("Storage cannot be null.");
        }
        if (select == null || select.getTypes().isEmpty()) {
            throw new IllegalArgumentException("Select cannot be null.");
        }
        if (select.getTypes().size() == 1) { // Optimization: no need to split (one 1 type selected)
            return storage.fetch(select);
        } else {
            // Build multiple selects based on types in original select
            List<Select> selects = new ArrayList<Select>(select.getTypes().size());
            Paging paging = select.getPaging();
            // No paging and start from beginning
            for (ComplexTypeMetadata currentType : select.getTypes()) {
                // Only remember the current type in query
                Select typeSelect = select.copy(); // Also copies conditions (if any)
                typeSelect.getTypes().clear();
                typeSelect.getTypes().add(currentType);
                // Start and limit to be handled by ResultsMerge (no need for paging in type's queries).
                typeSelect.getPaging().setStart(0);
                typeSelect.getPaging().setLimit(Integer.MAX_VALUE);
                selects.add(typeSelect);
            }
            return new ResultsMerge(selects, storage, paging.getStart(), paging.getLimit());
        }
    }

    private static class ResultsMerge implements StorageResults {

        private final Storage storage;

        private final List<Select> selects;

        private final Map<Select, Integer> queryToCount = new LinkedHashMap<Select, Integer>();

        public ResultsMerge(List<Select> selects, Storage storage, int start, int limit) {
            if (selects.isEmpty()) {
                throw new IllegalArgumentException("Expected several selects, got " + selects.size() + ".");
            }
            this.selects = new ArrayList<Select>(selects);
            this.storage = storage;
            // Init counts
            for (Select select : selects) {
                StorageResults results = storage.fetch(select);
                try {
                    queryToCount.put(select, results.getCount());
                } finally {
                    results.close();
                }
            }
            // Filter selects based on start and limit
            if (start > 0) {
                int currentIndex = 0;
                for (Map.Entry<Select, Integer> current : queryToCount.entrySet()) {
                    Integer currentCount = current.getValue();
                    if (currentIndex + currentCount < start) {
                        selects.remove(current.getKey()); // Skip this select
                        currentIndex += currentCount;
                    } else { // currentIndex + currentCount > start
                        int firstSelectIndex = selects.indexOf(current.getKey());
                        Select select = selects.get(firstSelectIndex);
                        select.getPaging().setStart(start - currentIndex);
                        int lastLimit = currentCount - start;
                        select.getPaging().setLimit(lastLimit);
                        while (limit - lastLimit > 0) {
                            int newLimit = limit - lastLimit;
                            selects.get(firstSelectIndex++).getPaging().setLimit(newLimit);
                            lastLimit += newLimit;
                        }
                        break;
                    }
                }
            }
        }

        @Override
        public int getSize() {
            int size = 0;
            for (Select select : selects) {
                StorageResults results = storage.fetch(select);
                try {
                    size += results.getSize();
                } finally {
                    results.close();
                }
            }
            return size;
        }

        @Override
        public int getCount() {
            int count = 0;
            for (Integer currentCount : queryToCount.values()) {
                count += currentCount;
            }
            return count;
        }

        @Override
        public void close() {
            // Nothing to do
        }

        @Override
        public Iterator<DataRecord> iterator() {
            return new MergeIterator(selects);
        }

        private class MergeIterator implements Iterator<DataRecord> {

            private final Iterator<Select> selects;

            private Iterator<DataRecord> currentIterator;

            public MergeIterator(List<Select> selectList) {
                selects = selectList.iterator();
            }

            @Override
            public boolean hasNext() {
                while ((currentIterator == null || !currentIterator.hasNext()) && selects.hasNext()) {
                    Select next = selects.next();
                    currentIterator = storage.fetch(next).iterator();
                }
                return currentIterator.hasNext() || selects.hasNext();
            }

            @Override
            public DataRecord next() {
                return currentIterator.next();
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        }
    }
}
