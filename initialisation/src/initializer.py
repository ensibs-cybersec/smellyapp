from flask import Flask

import MySQLdb

app = Flask(__name__)

@app.route('/', methods=['GET'])
def init():

    try:
        conn = MySQLdb.connect (host = "mysql", user = "root", passwd = "Ensibs56")
        cursor = conn.cursor()
        cursor.execute('DROP DATABASE IF EXISTS test')
        cursor.execute('CREATE DATABASE test')
        cursor.execute('USE test')
        cursor.execute('CREATE TABLE personnes (id INT UNSIGNED NOT NULL AUTO_INCREMENT, lastname VARCHAR(50), firstname VARCHAR(50), birthdate DATETIME, PRIMARY KEY (id))')
        cursor.execute("INSERT INTO personnes VALUES (1, 'Lagaffe', 'Gaston', '1970-01-01')")
        cursor.execute("INSERT INTO personnes VALUES (2, 'Gouigoux', 'Jean-Philippe', '1975-01-01')")
        conn.commit();
        conn.close();

    except MySQLdb.Error, e:
        print "Error %d: %s" % (e.args[0], e.args[1])

    return "Database 'test' initialized with dummy individuals"

@app.route('/data', methods=['GET'])
def data():

    try:
        conn = MySQLdb.connect (host = "mysql", user = "root", passwd = "Ensibs56", db = "test")
        cursor = conn.cursor()
        
        cursor.execute("SELECT * FROM personnes")

        if cursor.rowcount > 0:
            return "%i items found" % cursor.rowcount
        else:
            return "no item found"
            
    except MySQLdb.Error, e:
        print "Error %d: %s" % (e.args[0], e.args[1])

if __name__ == '__main__':
    app.run(host='0.0.0.0')
