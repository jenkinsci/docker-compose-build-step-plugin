#!/usr/bin/env python
# -*- coding: utf-8 -*-

"""
Docker Compose Jenkins Plugin
"""

import copy
import logging
import sys

from compose.cli.main import TopLevelCommand, project_from_options

__author__ = "João Galego <jgalego1990@gmail.com>"
__copyright__ = "João Galego"
__license__ = "mit"

#############
# Constants #
#############

# Logger
LOGGER = logging.getLogger(__name__)

# Docker Compose properties
OPT_NO_DEPS = "--no-deps"
OPT_ABORT_ON_CONTAINER_EXIT = "--abort-on-container-exit"
OPT_ALWAYS_RECREATE_DEPS = "--always-recreate-deps"
OPT_SCALE = "--scale"
OPT_SERVICE = "SERVICE"
OPT_REMOVE_ORPHANS = "--remove-orphans"
OPT_NO_RECREATE = "--no-recreate"
OPT_FORCE_RECREATE = "--force-recreate"
OPT_BUILD = "--build"
OPT_NO_BUILD = "--no-build"
OPT_NO_COLOR = "--no-color"
OPT_RMI = "--rmi"
OPT_VOLUMES = "--volumes"
OPT_FOLLOW = "--follow"
OPT_TIMESTAMPS = "--timestamps"
OPT_TAIL = "--tail"
OPT_DETACH = "--detach"
OPT_FILE = "--file"

def setup_logging(loglevel):
    """Setup basic logging
    :param loglevel (int) minimum loglevel for emitting messages
    """

    logformat = "[%(asctime)s] %(levelname)s %(name)s %(message)s"
    logging.basicConfig(level=loglevel, stream=sys.stdout,
                        format=logformat, datefmt="%Y-%m-%d %H:%M:%S")

class DockerCompose():
    """Docker Compose Wrapper Class"""

    def __init__(self, project_location):
        """Docker Compose Wrapper Class constructor
        :param project_location (str) compose file location
        """
        self.project_location = project_location
        self.options = {OPT_NO_DEPS: False,
                        OPT_ABORT_ON_CONTAINER_EXIT: False,
                        OPT_ALWAYS_RECREATE_DEPS: False,
                        OPT_SCALE: [],
                        OPT_SERVICE: "",
                        OPT_REMOVE_ORPHANS: False,
                        OPT_NO_RECREATE: True,
                        OPT_FORCE_RECREATE: False,
                        OPT_BUILD: False,
                        OPT_NO_BUILD: False,
                        OPT_NO_COLOR: False,
                        OPT_RMI: "none",
                        OPT_VOLUMES: "",
                        OPT_FOLLOW: False,
                        OPT_TIMESTAMPS: False,
                        OPT_TAIL: "all",
                        OPT_DETACH: True
                       }

        setup_logging(logging.INFO)

    def set_option(self, opt_name, opt_value):
        """Set Docker Compose option
        :param opt_name (str) option name
        :param opt_value option value"""
        self.options[opt_name] = opt_value

    def set_compose_file(self, filename):
        """Set compose file
        :param filename (str) the compose file name
        """
        LOGGER.info("Setting compose file to '%s'", filename)
        self.set_option(OPT_FILE, [filename])

    def top_level_command_from_options(self, options):
        """Creates a top level command from options
        :param options (str) docker compose options"""
        project = project_from_options(self.project_location, options)
        cmd = TopLevelCommand(project)
        return cmd

    def reset_compose_file(self):
        """Reset compose file"""
        try:
            del self.options[OPT_FILE]
        except KeyError:
            LOGGER.debug("Ignoring reset compose file")

    def start_service(self, service_name, scale=1):
        """Starts a service
        :param service_name (str) the name of the service
        :param scale (int) the number of containers to run for a service"""
        LOGGER.info("Starting service %s (scale: %d)", service_name, scale)
        start_service_options = copy.deepcopy(self.options)
        start_service_options[OPT_SERVICE] = [service_name]
        start_service_options[OPT_SCALE] = ["%s=%d" % (service_name, scale)]
        cmd = self.top_level_command_from_options(start_service_options)
        cmd.up(start_service_options)

    def start_all_services(self):
        """Starts all services"""
        LOGGER.info("Starting all services")
        cmd = self.top_level_command_from_options(self.options)
        cmd.up(self.options)

    def stop_service(self, service_name):
        """Stops a service
        :param service_name (str) the name of the service
        """
        LOGGER.info("Stopping service %s", service_name)
        stop_service_options = copy.deepcopy(self.options)
        stop_service_options[OPT_SERVICE] = [service_name]
        cmd = self.top_level_command_from_options(stop_service_options)
        cmd.down(stop_service_options)

    def stop_all_services(self):
        """Stops all services"""
        LOGGER.info("Stopping all services")
        cmd = self.top_level_command_from_options(self.options)
        cmd.down(self.options)
