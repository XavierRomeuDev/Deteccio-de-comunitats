package cat.urv.deim;

import cat.urv.deim.exceptions.ElementNoTrobat;
import java.util.Iterator;
import java.util.NoSuchElementException;
import cat.urv.deim.exceptions.PosicioForaRang;

public class LlistaOrdenada<E extends Comparable<E>> {
    private Node<E> fantasma;
    private int numElements;

    public LlistaOrdenada() {
        this.fantasma = new Node<>(null);
        this.numElements = 0;
    }

    public void inserir(E e) {
        Node<E> newNode = new Node<>(e);
        Node<E> prev = fantasma;
        Node<E> current = fantasma.next;

        while (current != null && current.data.compareTo(e) < 0) {
            prev = current;
            current = current.next;
        }

        prev.next = newNode;
        newNode.next = current;
        numElements++;
    }

    public void esborrar(E e) throws ElementNoTrobat {
        Node<E> prev = fantasma;
        Node<E> current = fantasma.next;

        while (current != null && !current.data.equals(e)) {
            prev = current;
            current = current.next;
        }

        if (current == null) {
            throw new ElementNoTrobat();
        }

        prev.next = current.next;
        numElements--;
    }

    public E consultar(int pos) throws PosicioForaRang {
        if (pos < 0 || pos >= numElements) {
            throw new PosicioForaRang();
        }

        Node<E> current = fantasma.next;
        for (int i = 0; i < pos; i++) {
            current = current.next;
        }

        return current.data;
    }

    public int buscar(E e) throws ElementNoTrobat {
        Node<E> current = fantasma.next;
        int pos = 0;
        while (current != null && !current.data.equals(e)) {
            current = current.next;
            pos++;
        }

        if (current == null) {
            throw new ElementNoTrobat();
        }

        return pos;
    }

    public boolean existeix(E e) {
        try {
            buscar(e);
            return true;
        } catch (ElementNoTrobat eNotFound) {
            return false;
        }
    }

    public boolean esBuida() {
        return numElements == 0;
    }

    public int numElements() {
        return numElements;
    }

    private static class Node<E> {
        E data;
        Node<E> next;

        Node(E data) {
            this.data = data;
            this.next = null;
        }
    }

    public Iterator<E> iterator() {
        return new LlistaOrdenadaIterator();
    }

    private class LlistaOrdenadaIterator implements Iterator<E> {
        private Node<E> current = fantasma.next;

        @Override
        public boolean hasNext() {
            return current != null;
        }

        @Override
        public E next() {
            if (!hasNext()) {
                throw new NoSuchElementException();
            }
            E data = current.data;
            current = current.next;
            return data;
        }
    }
}
