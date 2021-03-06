package no.oslomet.cs.algdat.Eksamen;

import com.sun.source.tree.BinaryTree;

import java.util.*;

public class EksamenSBinTre<T> {
    private static final class Node<T>   // en indre nodeklasse
    {
        private T verdi;                   // nodens verdi
        private Node<T> venstre, høyre;    // venstre og høyre barn
        private Node<T> forelder;          // forelder

        // konstruktør
        private Node(T verdi, Node<T> v, Node<T> h, Node<T> forelder) {
            this.verdi = verdi;
            venstre = v;
            høyre = h;
            this.forelder = forelder;
        }

        private Node(T verdi, Node<T> forelder)  // konstruktør
        {
            this(verdi, null, null, forelder);
        }

        @Override
        public String toString() {
            return "" + verdi;
        }

    } // class Node

    private Node<T> rot;                            // peker til rotnoden
    private int antall;                             // antall noder
    private int endringer;                          // antall endringer

    private final Comparator<? super T> comp;       // komparator

    public EksamenSBinTre(Comparator<? super T> c)    // konstruktør
    {
        rot = null;
        antall = 0;
        comp = c;
    }

    public boolean inneholder(T verdi) {
        if (verdi == null) return false;

        Node<T> p = rot;

        while (p != null) {
            int cmp = comp.compare(verdi, p.verdi);
            if (cmp < 0) p = p.venstre;
            else if (cmp > 0) p = p.høyre;
            else return true;
        }

        return false;
    }

    public int antall() {
        return antall;
    }

    public String toStringPostOrder() {
        if (tom()) return "[]";

        StringJoiner s = new StringJoiner(", ", "[", "]");

        Node<T> p = førstePostorden(rot); // går til den første i postorden
        while (p != null) {
            s.add(p.verdi.toString());
            p = nestePostorden(p);
        }

        return s.toString();
    }

    public boolean tom() {
        return antall == 0;
    }

    public boolean leggInn(T verdi) {
        Objects.requireNonNull(verdi, "Ulovlig med nullverdier!");

        Node<T> p = rot, q = null;               // p starter i roten
        int cmp = 0;                             // hjelpevariabel

        while (p != null)       // fortsetter til p er ute av treet
        {
            q = p;                                 // q er forelder til p
            cmp = comp.compare(verdi,p.verdi);     // bruker komparatoren
            p = cmp < 0 ? p.venstre : p.høyre;     // flytter p
        }

        // p er nå null, dvs. ute av treet, q er den siste vi passerte

        p = new Node<>(verdi,q); // oppretter en ny node

        if (q == null) rot = p;                  // p blir rotnode
        else if (cmp < 0) q.venstre = p;         // venstre barn til q
        else q.høyre = p;                        // høyre barn til q

        endringer++;
        antall++;                                // én verdi mer i treet
        return true;                             // vellykket innlegging
    }

//Oppgave 6
    public boolean fjern(T verdi) {
        if (verdi == null) return false;

        Node<T> p = rot, q = null;

        while (p != null)
        {
            int cmp = comp.compare(verdi, p.verdi);
            if (cmp < 0) {
                q = p;
                p = p.venstre;
            }
            else if (cmp > 0) {
                q = p;
                p = p.høyre;
            }
            else break;
        }
        if (p == null) return false;

        if(q == null && !(p.høyre != null && p.venstre != null) ){

            Node<T> b = null;
            if(p.høyre != null){
                b = p.høyre;
            }
            else if(p.venstre != null){
                b = p.venstre;
            }
            else{
                b = null;
            }
            if(b == null){
                antall = 0;
                rot = null;
                return true;
            }
            else{
                rot = b;
                b.forelder = null;
                antall--;
                return true;
            }

        }else if(p.venstre == null || p.høyre == null){

            Node<T> barn;
            if(p.venstre != null){
                barn = p.venstre;
                if(q.høyre == p){
                    q.høyre = barn;
                    barn.forelder = q;
                }
                else{
                    q.venstre = barn;
                    barn.forelder = q;
                }
            }
            else if(p.høyre != null){
                barn = p.høyre;
                if(q.høyre == p){
                    q.høyre = barn;
                    barn.forelder = q;
                }
                else{
                    q.venstre = barn;
                    barn.forelder = q;
                }
            }
            else{
                if(q.høyre == p){
                    q.høyre = null;
                }
                else{
                    q.venstre = null;
                }
            }
        }
        else {
            Node<T> r = p.høyre;
            Node<T> s = p;

            while(r.venstre != null){
                s = r;
                r = r.venstre;
            }

            p.verdi = r.verdi;
            if(s != p){
                if(r.høyre != null){
                    s.venstre = r.høyre;
                    r.høyre.forelder = s;
                }
                s.venstre = null;
            }
            else{
                if(r.høyre != null){
                    s.høyre = r.høyre;
                    r.høyre.forelder = s;
                }
                p.høyre = null;
            }
        }

        antall--;
        return true;
    }

//Oppgave 6
    public int fjernAlle(T verdi) {

        int antallFjernet = 0;

        while (fjern(verdi)){
            antallFjernet++;
        }
        return antallFjernet;
    }

    //Oppgave 2
    public int antall(T verdi) {

        int temp;
        int verdiForekomster;
        Node<T> p;

        if (verdi == null) {
            return 0;
        }

        temp = 0;
        verdiForekomster = 0;
        p = rot;

        while (p != null) {
            temp = comp.compare(verdi, p.verdi);

            if (temp > 0) {
                p = p.høyre;
            }
            else if (temp < 0) {
                p = p.venstre;
            }
            else {
                verdiForekomster++;
                p = p.høyre;
                while (p != null) {
                    temp = comp.compare(verdi, p.verdi);
                    if (temp == 0) {
                        verdiForekomster++;
                        p = p.høyre;
                    }
                    else {
                        p = p.venstre;
                    }
                }
            }
        }
        return verdiForekomster;
    }

//Oppgave 6
    public void nullstill() {
        if (rot == null){
            return;
        }

        ArrayDeque<Node<T>> queue = new ArrayDeque<>();

        queue.addLast(rot);

        while(!queue.isEmpty()) {
            Node<T> temp = queue.removeFirst();

            if (temp.venstre != null) {
                queue.addLast(temp.venstre);
            }
            if (temp.høyre != null) {
                queue.addLast(temp.høyre);
            }
            temp.verdi = null;
        }

        antall = 0;
        rot = null;

    }
// Oppgave 3
    private static <T> Node<T> førstePostorden(Node<T> p) {
        while ((p.venstre != null) || (p.høyre != null)) {
            while (p.venstre != null) {
                p = p.venstre;
            }
            if (p.høyre != null) {
                p = p.høyre;
            }
        }
        return p;
    }

    // Oppgave 3
    private static <T> Node<T> nestePostorden(Node<T> p) {
        if (p.forelder == null){
            return null;
        }
        else {
            if (p.forelder.høyre == p) {
                p = p.forelder;
            } else {
                if (p.forelder.høyre == null) {
                    p = p.forelder;
                } else {
                    p = førstePostorden(p.forelder.høyre);
                }

            }
        }

        return p;
    }

//Oppgave 4
    public void postorden(Oppgave<? super T> oppgave) {

        if (tom()){
            return;
        }

        Node<T> p = førstePostorden(rot);

        while (p != null){
            oppgave.utførOppgave(p.verdi);
            p = nestePostorden(p);
        }
    }


    public void postordenRecursive(Oppgave<? super T> oppgave) {
        postordenRecursive(rot, oppgave);
    }
// Oppgave 4
    private void postordenRecursive(Node<T> p, Oppgave<? super T> oppgave) {
        if (p == null) {
            return;
        }

        postordenRecursive(p.venstre, oppgave);
        postordenRecursive(p.høyre, oppgave);
        oppgave.utførOppgave(p.verdi);

    }
// Oppgave 5
    public ArrayList<T> serialize () {
        ArrayDeque<Node<T>> queue = new ArrayDeque<>();
        ArrayList<T> list = new ArrayList<>();

        queue.addLast(rot);

        while(!queue.isEmpty()) {
            Node<T> temp = queue.removeFirst();

            if (temp.venstre != null) {
                queue.addLast(temp.venstre);
            }
            if (temp.høyre != null) {
                queue.addLast(temp.høyre);
            }
            list.add(temp.verdi);
        }
        return list;
    }
// Oppgave 5
    static <K> EksamenSBinTre<K> deserialize(ArrayList<K> data, Comparator<? super K> c) {

        EksamenSBinTre<K> tre = new EksamenSBinTre<>(c);

        for (int i = 0; i < data.size(); i++) {
            tre.leggInn(data.get(i));
        }

        return tre;
    }


} // ObligSBinTre
