require('dotenv').config();
const express = require('express');
const expressWs = require('express-ws');
const websocket = require('./src/controllers/websocket');
const routers = require('./src/routers/index');
const { errorHandler } = require('./src/middlewares/error/errorHandler');

const app = express();
expressWs(app);
app.use(express.json());


app.get('/', (req, res) => {
    res.send('Hello World!');
});

app.ws('/ws/:chat_id', websocket)

app.use('/api', routers);
app.use(errorHandler)

const port = process.env.SERVER_PORT || 5000;

app.listen(port, () => {
    console.log(`Chat app listening at http://localhost:${port}`);
})