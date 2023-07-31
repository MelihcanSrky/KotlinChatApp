const pool = require('../../db/db');
const { v4: uuidv4 } = require('uuid');
const bcrypt = require('bcrypt');
const { CustomError } = require('../../middlewares/error/errorHandler');

const createUser = async (req, res, next) => {
    try {
        const { username, firstname, lastname, password } = req.body;
        uuid = uuidv4();
        const cryptPassword = await bcrypt.hash(password, 10);

        const userExists = await pool.query(
            "SELECT * FROM users WHERE username = $1",
            [username]
        );
        if (userExists.rows.length > 0) {
            res.status(401).json({
                status: 401,
                message: 'User name already exists',
                data: null
            })
            return;
        }

        const user = await pool.query(
            "INSERT INTO users (uuid, username, firstname, lastname, password) VALUES ($1, $2, $3, $4, $5) RETURNING *",
            [uuid, username, firstname, lastname, cryptPassword]
        );

        res.status(201).json({
            status: 201,
            message: 'User created successfully',
            data: user.rows[0]
        })
    } catch (error) {
        next(error);
    }
}

module.exports = {
    createUser
}