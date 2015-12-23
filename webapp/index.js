var express  = require('express');
var cookieParser = require('cookie-parser');
var app = express();
var mongoose = require('mongoose');
var port = process.env.PORT || 8080;
var http = require('http');
var md5 = require('md5');

var database = {
	url : 'mongodb://mongo:27017'
}

var morgan = require('morgan');
var bodyParser = require('body-parser');
var methodOverride = require('method-override');

// To be redesigned with a loop and a break on total timeout or number of tries
mongoose.connect(database.url, function(err) {
	if(err) {
		console.log('connection error (first try)', err);
		setTimeout(function() {
			mongoose.connect(database.url, function(err) {
				if(err) {
					console.log('connection error (second try)', err);
					setTimeout(function() {
						mongoose.connect(database.url, function(err) {
							if(err) {
								console.log('connection error (three strikes... you are out)', err);
							} else {
								console.log('successful connection (third try... almost out)');
							}
						});
					},5000);
				} else {
					console.log('successful connection (second try)');
				}
			});
		},1000);
	} else {
		console.log('successful connection (first try)');
	}
});

app.use(express.static(__dirname + '/public'));
app.use(morgan('dev'));
app.use(bodyParser.json()); // parse application/json
app.use(methodOverride('X-HTTP-Method-Override'));
app.use(cookieParser());

var mongoose = require('mongoose');
var MortgageModel = mongoose.model('Mortgage', {
	text: {type: String, default: ''},
	startMonth: {type: Number},
	repaymentLengthInMonths: {type: Number},
	amountBorrowed: {type: Number},
	isRateFixed: {type: Boolean},
	fixRate: {type: Number},
});

function getMortgages(res){
	MortgageModel.find(function(err, mortgages) {
		if (err)
			res.send(err)
		res.json(mortgages);
	});
};

app.get('/api/mortgages', function(req, res) {
	getMortgages(res);
});
app.post('/api/report', function(req, res) {
	MortgageModel.findOne({ 'text': req.body.reference }, function(err, mortgage) {
		var contentString = JSON.stringify(mortgage);
		var headers = {
			'Content-Type': 'application/json',
			'Content-Length': contentString.length
		};
		var options = {
		  host: 'notifier',
		  port: 5000,
		  path: '/repayment?email=' + req.body.email + '&repaid=' + req.body.repaid + '&newRate=' + req.body.newRate,
		  method: 'POST',
		  headers: headers
		};
		var repaymentRequest = http.request(options);
		repaymentRequest.write(contentString);
		repaymentRequest.end();
	});
});

function getAllPersonnes(res) {
	console.log('====================> Appel de getAllPersonnes');
	var options = {
		host: 'personnes',
		port: 5000,
		path: '/api/personnes',
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
};

app.get('/api/personnes', function(req, res) {
    getAllPersonnes(res);
});

app.post('/api/report', function(req, res) {
	MortgageModel.findOne({ 'text': req.body.reference }, function(err, mortgage) {
		var contentString = JSON.stringify(mortgage);
		var headers = {
			'Content-Type': 'application/json',
			'Content-Length': contentString.length
		};
		var options = {
		  host: 'notifier',
		  port: 5000,
		  path: '/repayment?email=' + req.body.email + '&repaid=' + req.body.repaid + '&newRate=' + req.body.newRate,
		  method: 'POST',
		  headers: headers
		};
		var repaymentRequest = http.request(options);
		repaymentRequest.write(contentString);
		repaymentRequest.end();
	});
});
// app.post('/api/mortgages', function(req, res) {
// 	MortgageModel.create({
// 		text : req.body.text,
// 		startMonth: req.body.start,
// 		repaymentLengthInMonths: req.body.duration,
// 		amountBorrowed: req.body.amount,
// 		isRateFixed: req.body.isRateFixed,
// 		fixRate: req.body.rate
// 	}, function(err, mortgage) {
// 		if (err)
// 			res.send(err);
// 		getMortgages(res);
// 	});
// });

function createPersonne(req) {
	var contentString = '{\"lastname\": \"' + req.body.lastname + '\", \"firstname\": \"' + req.body.firstname + '\", \"birthdate\": \"' + req.body.birthdate + '\"}';
	var headers = {
		'Content-Type': 'application/json',
		'Content-Length': contentString.length
	};
	var options = {
		host: 'personnes',
		port: 5000,
		path: '/api/personnes',
		method: 'POST',
		headers: headers
	};
	var repaymentRequest = http.request(options);
	repaymentRequest.write(contentString);
	repaymentRequest.end();	
};

app.post('/api/mortgages', function(req, res) {
	createPersonne(req).then(function() {
		getAllPersonnes(res);
	});
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
		console.log(mdp);
		var hash = md5(mdp); // Ajouter un sel
		console.log(hash);
		if (hash == '721a9b52bfceacc503c056e3b9b93cfa') {
			res.cookie('connected', 1);
			res.redirect('/');
		} else {
			res.send('<form><input type="text" name="login"/><input type="password" name="password"/><input type="submit" value="Connect"/></form>');
		}
	}
});
app.get('*', function(req, res) {
	console.log("Cookies : ", req.cookies);
	if (req.cookies.connected) {
		res.sendfile('./public/annuaire.html');
	} else {
		res.redirect('/login');
	}
});

app.listen(port);
console.log("App listening on port " + port);
