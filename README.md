# xrandr-parser-clj

Parse basic xrandr info from cli output to edn format.

I made this to satisfy my own need - a simple babashka script for managing display configurations.

## Usage

Paste the contents of `xrandr_parser.clj` into your project or [`babashka`](https://github.com/borkdude/babashka) script.

Extend at you own leisure.

### Example

```clojure
(require '[clojure.pprint :refer [pprint]])

(defn connected-displays []
  (->> (shell/sh "xrandr --verbose")
       :out
       parse-xrandr
       (filter :is-connected)))

(pprint (connected-displays))
```
