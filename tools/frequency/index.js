const async = require('async');
const fs = require('fs');

async.map(['google-10000-english.txt', 'stopwords.txt'],
    (filename, cb) => fs.readFile(filename, 'utf8', cb),
    (err, files) => {
        if (err) {
            return console.error(err);
        }

        const words = files[0].split(/\r?\n/g);
        const stopwords = files[1].split(/\r?\n/g);

        const filteredWords = words
            .filter((word) => !stopwords.includes(word))
            .filter((word) => word.length > 1);

        const steps = filteredWords.reduce((acc, word) => {
            if (acc[acc.length - 1].length === 200) {
                acc.push([]);
            }

            acc[acc.length - 1].push(word);
            return acc;
        }, [[]]);

        console.log(steps);
    });