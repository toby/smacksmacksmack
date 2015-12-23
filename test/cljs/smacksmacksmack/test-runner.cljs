(ns smacksmacksmack.test-runner
  (:require
   [cljs.test :refer-macros [run-tests]]
   [smacksmacksmack.core-test]))

(enable-console-print!)

(defn runner []
  (if (cljs.test/successful?
       (run-tests
        'smacksmacksmack.core-test))
    0
    1))
