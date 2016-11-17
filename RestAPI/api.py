from flask import Flask
from flask_restful import Resource, Api

app = Flask(__name__)
api = Api(app)

class CreateUser(Resource):
    def post(self):
        return {'status': 'success'}
    def get(self):
        return {'hello': 'world'}

api.add_resource(CreateUser, '/CreateUser')

if __name__ == '__main__':
    app.run(debug=True)

