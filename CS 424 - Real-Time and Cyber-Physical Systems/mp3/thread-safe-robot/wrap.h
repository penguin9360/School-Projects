// Execute around paradigm http://stroustrup.com/wrapper.pdf

template <class T, class Pref, class Suf> class Wrap;
template <class T, class Suf> class Call_proxy {
  T *p;
  mutable bool own;
  Suf suffix;
  Call_proxy(T *pp, Suf su)
      : p(pp), own(true), suffix(su) {} // restrict creation
  Call_proxy &operator=(const Call_proxy &);
  // prevent assignment
public:
  template <class U, class P, class S> friend class Wrap;
  Call_proxy(const Call_proxy &a) : p(a.p), own(true), suffix(a.suffix) {
    a.own = false;
  }
  ~Call_proxy() {
    if (own)
      suffix();
  }
  T *operator->() const { return p; }
};

template <class T, class Pref, class Suf> class Wrap {
  T *p;
  int *owned;
  void incr_owned() const {
    if (owned)
      ++*owned;
  }
  void decr_owned() const {
    if (owned && --*owned == 0) {
      delete p;
      delete owned;
    }
  }
  Pref prefix;
  Suf suffix;

public:
  Wrap(T &x, Pref pr, Suf su) : p(&x), owned(0), prefix(pr), suffix(su) {}
  Wrap(T *pp, Pref pr, Suf su)
      : p(pp), owned(new int(1)), prefix(pr), suffix(su) {}
  Wrap(const Wrap &a)
      : p(a.p), owned(a.owned), prefix(a.prefix), suffix(a.suffix) {
    incr_owned();
  }
  Wrap &operator=(const Wrap &a) {
    a.incr_owned();
    decr_owned();
    p = a.p;
    owned = a.owned;
    prefix = a.prefix;
    ;
    suffix = a.suffix;
    return *this;
  }
  ~Wrap() { decr_owned(); }
  Call_proxy<T, Suf> operator->() const {
    prefix();
    return Call_proxy<T, Suf>(p, suffix);
  }
  T *direct() const { return p; } // extract pointer to wrapped object
};
