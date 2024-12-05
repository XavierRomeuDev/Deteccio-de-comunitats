package cat.urv.deim;

import cat.urv.deim.exceptions.ElementNoTrobat;
import cat.urv.deim.exceptions.PosicioForaRang;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class LlistaNoOrdenada<E extends Comparable<E>> implements ILlistaGenerica<E>, Comparable<LlistaNoOrdenada<E>> {
    private Node<E> fantasma;
    private int numElements;

    public LlistaNoOrdenada() {
        this.fantasma = new Node<>(null);
        this.numElements = 0;
    }

    public void inserir(E e) {
        Node<E> newNode = new Node<>(e);
        newNode.next = fantasma.next;
        fantasma.next = newNode;
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

    public E get(int pos) throws PosicioForaRang {
        if (pos < 0 || pos >= numElements) {
            throw new PosicioForaRang();
        }

        Node<E> current = fantasma.next;
        for (int i = 0; i < pos; i++) {
            current = current.next;
        }

        return current.data;
    }

    // public void unir(LlistaNoOrdenada<E> otraLista) {
    // // Obtener el último nodo de la lista actual
    // Node<E> ultimoNodo = this.fantasma;
    // while (ultimoNodo.next != null) {
    // ultimoNodo = ultimoNodo.next;
    // }

    // // Agregar todos los elementos de la otra lista al final de la lista actual
    // Node<E> nodoActual = otraLista.fantasma.next;
    // while (nodoActual != null) {
    // Node<E> nuevoNodo = new Node<>(nodoActual.data);
    // ultimoNodo.next = nuevoNodo;
    // ultimoNodo = nuevoNodo;
    // nodoActual = nodoActual.next;
    // }

    // // Actualizar el número de elementos en la lista actual
    // this.numElements += otraLista.numElements();
    // }

    private static class Node<E> {
        E data;
        Node<E> next;

        Node(E data) {
            this.data = data;
            this.next = null;
        }
    }

    public Iterator<E> iterator() {
        return new LlistaNoOrdenadaIterator();
    }

    private class LlistaNoOrdenadaIterator implements Iterator<E> {
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

    @Override
    public int compareTo(LlistaNoOrdenada<E> otraLista) {
        int numElementsLlista = this.numElements();
        int numElementsAltraLista = otraLista.numElements();

        if (numElementsLlista < numElementsAltraLista) {
            return -1;
        } else if (numElementsLlista > numElementsAltraLista) {
            return 1;
        } else {
            return 0;
        }
    }
}
