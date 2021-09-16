package scam.lisp_objects;

import scam.exceptions.LispError;
import scam.exceptions.WrongNofArgsError;
import scam.exceptions.WrongTypeError;

import java.util.Iterator;

/**
 * Implements a Scheme Cons object. A Cons object is essentially a pair, of
 * which the first and second element are called car and cdr. A Cons can be
 * used to implement a linked list terminated by a NIL object.
 */
public class Cons implements LispObject, Iterable<LispObject> {

    private LispObject car = null, cdr = null;

    public boolean tail = false;

    /**
     * Construct cons with given car and cdr.
     *
     * @param car .
     * @param cdr .
     */
    public Cons(LispObject car, LispObject cdr) {
        if (car == null || cdr == null) throw new NullPointerException();
        this.car = car;
        this.cdr = cdr;
    }

    /**
     * Construct cons with given car and set cdr to NIL.
     *
     * @param car .
     */
    public Cons(LispObject car) {
        if (car == null) throw new NullPointerException();
        this.car = car;
        cdr = NIL.instance;
    }

    /**
     * Construct cons with car and cdr set to null (warning: this should
     * only be used if it is certain that car and cdr are set afterwards).
     */
    public Cons() {
    }

    /**
     * @return Car (first element).
     */
    public LispObject getCar() {
        return car;
    }

    /**
     * @return Car (second element).
     */
    public LispObject getCadr() throws LispError {
        if (cdr == NIL.instance) throw new WrongNofArgsError();
        return cdr.asCons().car;
    }

    /**
     * @return Cdr (second element)
     */
    public LispObject getCdr() {
        return cdr;
    }

    public String toString() {
        if ((cdr == null) || (car == null)) return "This should never happen";
        if (cdr == NIL.instance) return "(" + car + ")";
        if (!(cdr instanceof Cons)) return "(" + car + " . " + cdr + ")";
        return "(" + car + " " + ((Cons) cdr).tailToString();
    }

    public String tailToString() {
        if (cdr == NIL.instance) return car + ")";
        if (!(cdr instanceof Cons)) return car + " . " + cdr + ")";
        return car + " " + ((Cons) cdr).tailToString();
    }

    /**
     * Returns the size of the linked list, if this object represents
     * the start of the linked list. Result is undefined if object does
     * not represent the start of a linked list.
     *
     * @return Size of cons as linked list.
     */
    public int size() throws WrongTypeError {
        int s = 1;
        LispObject c = this.cdr;
        while (c.isCons()) {
            c = c.asCons().getCdr();
            s++;
        }

        if (c != NIL.instance)
            throw new RuntimeException("Improper list.");

        return s;
    }

    /**
     * Return object at index i of the linked list of which this cons
     * represents the first element. Throws exception if cons does not
     * represent a proper linked list (i.e. not closed by NIL).
     *
     * @param index .
     * @return Object at index i of linked list.
     * @throws LispError .
     */
    @Deprecated
    public LispObject objectAt(int index) throws LispError {
        if (index == 0) return car;
        if (cdr instanceof Cons) return ((Cons) cdr).objectAt(index - 1);
        if (cdr == NIL.instance) throw new WrongNofArgsError();
        throw new LispError("Improper list");
    }

    /**
     * Append object to the linked list of which this cons
     * represents the first element. Throws exception if cons does not
     * represent a proper linked list (i.e. not closed by NIL).
     *
     * @param o Object to append.
     * @throws LispError .
     */
    public void append(LispObject o) throws LispError {
        if (car == null) {
            car = o;
            cdr = NIL.instance;
        } else if (cdr instanceof Cons) {
            ((Cons) cdr).append(o);
        } else if (cdr == NIL.instance) {
            cdr = new Cons(o);
        } else {
            throw new LispError("Improper list");
        }
    }

    @Override
    public Iterator<LispObject> iterator() {
        return new ConsIterator(this);
    }

    private static class ConsIterator implements Iterator<LispObject> {
        LispObject lispObjects;

        public ConsIterator(Cons lispObjects) {
            this.lispObjects = lispObjects;
        }

        @Override
        public boolean hasNext() {
            try {
                return lispObjects.isCons();
            } catch (RuntimeException e) {
                throw new RuntimeException("Improper list.");
            }
        }

        @Override
        public LispObject next() {
            try {
                LispObject elem = lispObjects.asCons().getCar();
                lispObjects = lispObjects.asCons().cdr;
                return elem;
            } catch (WrongTypeError e) {
                throw new RuntimeException("Improper list.");
            }
        }
    }
}