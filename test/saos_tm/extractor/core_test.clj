(ns saos-tm.extractor.core-test
  (:require [clojure.test :refer :all]
            [clojure.string :as str ]
            [saos-tm.extractor.common :refer :all]
            [saos-tm.extractor.law-links :refer :all]
            [saos-tm.extractor.law-links-trainset :refer :all]
            [langlab.core.parsers :refer [ lg-split-tokens-bi ] ]))

(deftest article-coords-test
  (is(=
    [["4" "0" "0" "2" "0" "0"]]
    (extract-coords "art. 4 pkt 2")))
  (is(=
    [["14a" "0" "0" "0" "0" "0"]]
    (extract-coords "art. 14a ")))
  (is(=
    [["50" "0" "1" "1" "0" "0"]]
    (extract-coords "art. 50 ust. 1 pkt 1 ")))
  (is(=
    [["3" "0" "2-3" "0" "0" "0"]]
    (extract-coords "art. 3 ust. 2-3 ")))
  (is(=
    [["103-105" "0" "0" "0" "0" "0"]]
    (extract-coords "art. 103-105 ")))
  (is(=
    [["47" "0" "1" "2" "0" "0"] ["47" "0" "1" "3" "0" "0"]]
    (extract-coords "art. 47 ust. 1 pkt 2 i 3 ")))
  (is(=
    [["0" "44" "1" "1" "0" "0"]]
    (extract-coords "§ 44 ust. 1 pkt 1 ")))
  (is(=
    [["0" "25" "3" "0" "0" "0"]]
    (extract-coords "§ 25 ust. 3 ")))
  (is(=
    [["0" "68a" "1" "0" "0" "0"]]
    (extract-coords "§ 68a ust. 1 ")))
  (is(=
    [["0" "79" "0" "0" "0" "0"]]
    (extract-coords "§ 79 ")))
  (is(=
    [["0" "34" "3" "2" "0" "0"]]
    (extract-coords "§ 34 ust. 3 pkt 2 ")))
  (is(=
    [["37" "4a" "0" "0" "0" "0"] ["37" "4b" "0" "0" "0" "0"]]
    (extract-coords "art. 37 § 4a, 4b ")))
  (is(=
    [["56" "1-3" "0" "0" "0" "0"]]
    (extract-coords "art. 56 § 1-3 ")))
  (is(=
    [["77" "1" "0" "0" "0" "0"] ["77" "2" "0" "0" "0" "0"]
     ["77" "2a" "0" "0" "0" "0"] ["77" "3" "0" "0" "0" "0"]
     ["77" "3a" "0" "0" "0" "0"] ["77" "6" "0" "0" "0" "0"]
     ["77" "7a" "0" "0" "0" "0"] ["77" "7b" "0" "0" "0" "0"]]
    (extract-coords "art. 77 § 1, 2, 2a, 3, 3a, 6, 7a i 7b ")))
  (is(=
    [["46" "1" "0" "0" "1" "0"]]
    (extract-coords "art. 46 § 1 zd. 1 ")))
  (is(=
    [["178" "0" "1" "0" "0" "0"] ["91" "0" "1" "0" "0" "0"]]
    (extract-coords "art. 178 ust. 1 i art. 91 ust. 1")))
  (is(=
    [["84" "0" "0" "0" "0" "0"] ["92" "0" "1" "0" "0" "0"]
     ["31" "0" "3" "0" "0" "0"]]
    (extract-coords "art. 84, art. 92 ust. 1 i art. 31 ust. 3")))
  (is(=
    [["2" "0" "0" "0" "0" "0"] ["84" "0" "0" "0" "0" "0"]
     ["91" "0" "1" "0" "0" "0"] ["178" "0" "1" "0" "0" "0"]]
    (extract-coords "art. 2, art. 84, z art. 91 ust. 1, art. 178 ust. 1")))
  (is(=
    '(("64" "0" "2" "0" "0" "0") ("64" "0" "3" "0" "0" "0")
      ("84" "0" "0" "0" "0" "0"))
  	(extract-coords "art. 64 ust. 2 i 3 oraz art. 84")))
  (is(=
    '(("64" "0" "2" "0" "0" "a"))
    (extract-coords "art. 64 ust. 2 lit. a")))
  ; (is(= [[]] (extract-coords "")))
  )

(deftest article-ranges-test []
  (let [
        tokens
          (lg-split-tokens-bi "pl"
            (str "jest niezgodny z art. 2, art. 31 ust. 3, art. 64 "
                 "ust. 1, 2 i 3, art. 84, art. 91 ust. 1, art. 92 ust. 1, "
                 "art. 178 ust. 1 Konstytucji i art. 13 pkt 1 i 3 aneksu A "
                 "„Ogólne warunki odnoszące się do memorandum finansowego” "
                 "do Umowy Ramowej"))]
  (is (=
    [[3 49][51 58]]
    (get-correct-coords-ranges tokens)))))

(deftest signature-extraction-test []
  (is (=
    "K. 1/00"
    (extract-signature "Sygn. K. 1/00 ")))
  (is (=
    "W. 17/92"
    (extract-signature "Sygn. akt (W. 17/92)")))
  (is (=
    "SK 22/06"
    (extract-signature "Sygn. akt SK 22/06*")))
  (is (=
    "Tw 51/12"
    (extract-signature "Sygn. akt Tw 51/12")))
  (is (=
    "W. 17/92"
    (extract-signature "Sygn. akt (W. 17/92)"))))

(deftest get-line-with-signature-test []
  (is (=
    "(K. 1/91)"
    (get-line-with-signature (slurp "unit-tests-data/K_01_91.txt"))))
  (is (=
    "(K. 1/92)"
    (get-line-with-signature (slurp "unit-tests-data/K_01_92.txt"))))
  (is (=
    "Sygn. akt (K. 1 /93)"
    (get-line-with-signature (slurp "unit-tests-data/K_01_93.txt")))))

(deftest extract-law-links-test []
  (is(=
    '({:act {:poz "1656", :nr "237"}, :art {:lit "0", :zd "0", :pkt "0", :ust "1-6", :par "0", :art "3"}}
      {:act {:poz "1656", :nr "237"}, :art {:lit "0", :zd "0", :pkt "5", :ust "0", :par "0", :art "4"}}
      {:act {:poz "1656", :nr "237"}, :art {:lit "0", :zd "0", :pkt "6", :ust "0", :par "0", :art "4"}}
      {:act {:poz "1656", :nr "237"}, :art {:lit "0", :zd "0", :pkt "0", :ust "0", :par "0", :art "57"}})
  (:extracted-links (extract-law-links
    (str "art. 3 ust. 1-6, art. 4 pkt 5 i 6, art. 57 ustawy z dnia 19 grudnia"
         " 2008 r. o emeryturach pomostowych (Dz. U. Nr 237, poz. 1656)")))))
  (is(=
    '({:act {:poz "1656", :nr "237"}, :art {:lit "0", :zd "0", :pkt "0", :ust "4-6", :par "0", :art "3"}}
      {:act {:poz "1656", :nr "237"}, :art {:lit "0", :zd "0", :pkt "6", :ust "0", :par "0", :art "4"}}
      {:act {:poz "483", :nr "78"}, :art {:lit "0", :zd "0", :pkt "0", :ust "0", :par "0", :art "2"}}
      {:act {:poz "483", :nr "78"}, :art {:lit "0", :zd "0", :pkt "0", :ust "1", :par "0", :art "32"}}))
  (:extracted-links (extract-law-links
    (str "art. 3 ust. 4-6 i art. 4 pkt 6 ustawy z dnia 19 grudnia 2008 r. "
         "o emeryturach pomostowych (Dz. U. Nr 237, poz. 1656) "
         "z art. 2 i art. 32 ust. 1 Konstytucji")))))



 