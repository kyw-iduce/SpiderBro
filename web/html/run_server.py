#!/usr/bin/env python

import BaseHTTPServer
import SimpleHTTPServer

"""
Usage:
    python run_server.py [port] 

Example:
    python run_server.py 8000 

N.B.: Mapping is hardcoded

"""


class CustomHTTPRequestHandler(
    SimpleHTTPServer.SimpleHTTPRequestHandler
):

    def end_headers(self):
        self.send_new_headers()

        SimpleHTTPServer\
            .SimpleHTTPRequestHandler\
            .end_headers(self)

    def send_new_headers(self):
        headers_by_path = {
            '/saturday/1.html': 'x-robots-tag: index, nofollow',
            '/sunday/1.html': 'x-robots-tag: noindex, follow'
        }

        request_path = self.path
        if request_path in headers_by_path:
            key, value = headers_by_path[request_path]\
                .split(":", 1)
            self.send_header(key, value)


if __name__ == '__main__':
    BaseHTTPServer.test(
        HandlerClass=CustomHTTPRequestHandler,
        ServerClass=BaseHTTPServer.HTTPServer,
        protocol="HTTP/1.1")