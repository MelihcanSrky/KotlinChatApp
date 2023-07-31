class CustomError extends Error {
  constructor(message, statusCode) {
    super(message);
    this.statusCode = statusCode;
  }
}

const errorHandler = (err, req, res, next) => {
    const statusCode = err.statusCode || 500;
    const message = err.message || "Internal Server Error";

    res.setHeader('Content-Type', 'application/json');
    res.status(statusCode).send({
        status: statusCode,
        message: message,
        data: null,
    })
}

module.exports = {
    CustomError,
    errorHandler
}