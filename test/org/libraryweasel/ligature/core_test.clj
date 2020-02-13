; Copyright (c) 2019-2020 Alex Michael Berry
;
; This program and the accompanying materials are made
; available under the terms of the Eclipse Public License 2.0
; which is available at https://www.eclipse.org/legal/epl-2.0/
;
; SPDX-License-Identifier: EPL-2.0

(ns org.libraryweasel.ligature.core-test
  (:require [clojure.test :refer :all]
            [org.libraryweasel.ligature.core :as l]
            [clojure.spec.alpha :as s]))

(deftest identifier?-test
  (testing "Common examples"
    (is (not (l/identifier? "")))
    (is (l/identifier? "http://localhost/people/7"))
    (is (not (l/identifier? "http://localhost(/people/7")))
    (is (not (l/identifier? "http://localhost /people/7")))
    (is (l/identifier? "hello"))
    (is (l/identifier? "_:"))
    (is (l/identifier? "_:valid"))
    (is (l/identifier? "_:1"))
    (is (l/identifier? "_:1344")))) ; TODO more test cases

(deftest lang-literal?-test
  (testing "Common examples"
    (is (not (l/lang-literal? "not a lang lit")))
    (is (not (l/lang-literal? {:value "" :lang ""})))
    (is (l/lang-literal? {:value "Hello" :lang "en"}))
    (is (not (l/lang-literal? {:value "Bonjour" :lang "fr" :type "fr"}))))) ; TODO more test cases

(deftest typed-literal?-test
  (testing "Common examples"
    (is (not (l/typed-literal? "not a typed literal")))
    (is (not (l/typed-literal? {})))
    (is (l/typed-literal? {:value "Hello" :type "identifier"}))
    (is (not (l/typed-literal? {:value "56" :type "number" :lang "en"}))))) ; TODO more test cases

(deftest statement?-test
  (testing "Common examples"
    (is (s/valid? ::l/statement ["hello" "world" "triple"]))
    (is (s/valid? ::l/statement ["hello" "world" "triple" "graph"]))
    (is (not (s/valid? ::l/statement [])))
    (is (not (s/valid? ::l/statement ["g"])))
    (is (not (s/valid? ::l/statement ["test" "test"])))
    (is (not (s/valid? ::l/statement ["test" "test" "g" "h" "e"])))
    (is (not (s/valid? ::l/statement [5 3 66 554])))
    (is (not (s/valid? ::l/statement ["test" "test" :a])))
    (is (s/valid? ::l/statement ["test" :a "test" "test"])))) ; TODO more test cases

(deftest statements?-test
  (testing "Common examples"
    (is (s/valid? ::l/statements [["hello" "world" "triple"]]))
    (is (s/valid? ::l/statements #{["hello" "world" "triple"]}))
    (is (s/valid? ::l/statements '(["hello" "world" "triple"]))))) ; TODO more test cases

(deftest lang-tag?-test
  (testing "Common examples"
    (is (not (l/lang-tag? "")))
    (is (l/lang-tag? "en"))
    (is (not (l/lang-tag? "en-")))
    (is (l/lang-tag? "en-fr"))
    (is (not (l/lang-tag? "en-fr-")))
    (is (l/lang-tag? "en-fr-sp"))
    (is (l/lang-tag? "ennnenefnk-dkfjkjfl-dfakjelfkjalkf-fakjeflkajlkfj"))
    (is (not (l/lang-tag? "en-fr-ef ")))))
