from flask import Flask
import MySQLdb

app = Flask(__name__)

@app.route('/', methods=['GET'])
def init():

    try:
        conn = MySQLdb.connect (host = "mysql", user = "ensibs", passwd = "Ensibs56", db = "test")
        cursor = conn.cursor()
        cursor.execute("DROP TABLE IF EXISTS PERSON")
        cursor.execute("CREATE TABLE PERSON (id INT UNSIGNED NOT NULL AUTO_INCREMENT, lastname VARCHAR(50), firstname VARCHAR(50), birthdate DATETIME, PRIMARY KEY (id))")
        cursor.execute("INSERT INTO PERSON VALUES (1, 'Lagaffe', 'Gaston', '1970-01-01')")
        cursor.execute("INSERT INTO PERSON VALUES (2, 'Gouigoux', 'Jean-Philippe', '1974-12-25')")
        conn.commit();
        conn.close();

    except MySQLdb.Error, e:
        print "Error %d: %s" % (e.args[0], e.args[1])
        return "Error %d: %s" % (e.args[0], e.args[1])
        
    return "Database 'test' initialized with dummy individuals"

if __name__ == '__main__':
    app.run(host='0.0.0.0')
