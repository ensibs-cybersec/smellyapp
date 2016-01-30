var express  = require('express');
var cookieParser = require('cookie-parser');
var app = express();
var port = process.env.PORT || 8080;
var http = require('http');
var md5 = require('md5');

var request = require('request');
var morgan = require('morgan');
var bodyParser = require('body-parser');
var methodOverride = require('method-override');

app.use(express.static(__dirname + '/public'));
app.use(morgan('dev'));
app.use(bodyParser.json()); // parse application/json
app.use(methodOverride('X-HTTP-Method-Override'));
app.use(cookieParser());

app.get('/api/person', function(req, res) {
	var options = {
		host: 'persons-directory',
		port: 5000,
		path: '/api/person',
		method: 'GET'
	};
	http.get(options, function(result) {
        result.setEncoding('utf8');
        var body = '';
        result.on('data', function(d) {
            body += d;
        });
        result.on('end', function() {
            try {
                var parsed = JSON.parse(body);
            } catch (err) {
                console.error('Unable to parse response as JSON', err);
            }
			res.json(parsed);
        });
    }).on('error', function(err) {
        console.error('Error with the request:', err.message);
    });
});

function createPerson(person, callback) {
	var query = { json: { lastname: person.lastname, firstname: person.firstname, birthdate: person.birthdate },
        url : 'http://persons-directory:5000/api/person', 
        method : 'POST' };
	request(query, callback);	
};

app.post('/api/person', function(req, res) {
	createPerson(req.body, function(error, response) { 
		if (error) return res.send(500, error);
		res.send(201, response.body);
	});
});

app.delete('/api/person', function(req, res) {
    res.send(501, "Not implemented yet");
});

app.get('/logout', function(req, res) {
	res.clearCookie('connected');
	res.send('<p>disconnected</p>');
});

app.get('/login', function(req, res) {
	if (req.cookies.connected) {
		res.redirect('/');
	} else {
		var mdp = req.param('password');
		var hash = md5(mdp); // Add salt
		if (hash == '721a9b52bfceacc503c056e3b9b93cfa') { // Hash for coucou
			res.cookie('connected', 1);
			res.redirect('/');
		} else {
			res.send('<form><input type="text" name="login"/><input type="password" name="password"/><input type="submit" value="Connect"/></form>');
		}
	}
});

app.get('*', function(req, res) {
	if (req.cookies.connected) {
		res.sendfile('./public/directory.html');
	} else {
		res.redirect('/login');
	}
});

app.listen(port);
console.log("App listening on port " + port);
