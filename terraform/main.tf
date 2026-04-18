terraform {
  cloud {
    organization = "mackmarton"
    workspaces {
      name = "picture-viewer"
    }
  }
  required_providers {
    heroku = {
      source  = "heroku/heroku"
      version = "~> 5.0"
    }
  }
}

variable "heroku_api_key" {}
variable "heroku_email" {}
variable "cloudinary_api_key" {}
variable "cloudinary_api_secret" {}
variable "cloudinary_cloud_name" {}
variable "app_name" {
  default = "picture-viewer-iac"
}

provider "heroku" {
  api_key = var.heroku_api_key
  email   = var.heroku_email
}

resource "heroku_app" "picture_viewer" {
  name   = var.app_name
  region = "eu"

  config_vars = {
    CLOUDINARY_API_KEY = var.cloudinary_api_key
    CLOUDINARY_API_SECRET = var.cloudinary_api_secret
    CLOUDINARY_CLOUD_NAME = var.cloudinary_cloud_name
  }
}

resource "heroku_addon" "postgres_db" {
  app_id  = heroku_app.picture_viewer.id
  plan    = "heroku-postgresql:essential-0"
}
