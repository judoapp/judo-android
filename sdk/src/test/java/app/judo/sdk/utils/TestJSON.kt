@file:Suppress("SpellCheckingInspection")

package app.judo.sdk.utils

object TestJSON {

    val syncResponse = """{
  "data": [
    {
      "url": "https://test1.judo.app/testexperience",
      "removed": false,
      "priority": 10
    }
  ],
  "nextLink": "https://test1.judo.app/sync?cursor=MjAyMC0xMS0yMFQxNjo0NDozNi44ODBa"
}""".trimMargin()


    const val color = """{
                    "red": 1,
                    "green": 1,
                    "blue": 1,
                    "alpha": 1
                }"""

    const val colorVariants = """{
   "systemName": "systemBackground",
   "default": {
        "red": 0.1,
        "green": 0.2,
        "blue": 0.3, 
        "alpha": 1
    },
    "highContrast": {
        "red": 0.3,
        "green": 0.2,
        "blue": 0.1, 
        "alpha": 1
    },
    "darkMode": {
        "red": 0.4,
        "green": 0.5,
        "blue": 0.6, 
        "alpha": 1
    },
    "darkModeHighContrast": {
        "red": 0.1,
        "green": 0.2,
        "blue": 0.3, 
        "alpha": 1
    }
}"""

    const val shadow = """{
    "color": {
        "systemName": "systemBackground",
        "default": {
            "red": 0.1,
            "green": 0.2,
            "blue": 0.3,
            "alpha": 0.4
        }
    },
    "x": 2,
    "y": 2,
    "blur": 3
}"""

    const val judo_message = """{"action": "SYNC"}"""

    const val screen = """{
  "backgroundColor": {
    "systemName": "systemBackground",
    "default": {
      "red": 1,
      "green": 1,
      "blue": 1,
      "alpha": 1
    },
    "darkMode": {
      "red": 0,
      "green": 0,
      "blue": 0,
      "alpha": 1
    }
  },
  "name": "Screen",
  "id": "3B19D34C-F859-42E6-90CE-0576DF70C13E",
  "childIDs": [
    "7DEADBAB-15B8-4333-BC68-6156F74E5A26"
  ],
  "modalPresentationStyle": "sheet",
  "__typeName": "Screen",
  "statusBarStyle": "default"
}"""

    const val action_Close = """{
  "__typeName": "CloseAction"
}"""

    const val action_PresentWebsite = """{
  "__typeName": "PresentWebsiteAction",
  "url": "https://www.example.com/",
  "modalTransitionStyle": "systemDefault"
}"""

    const val action_openURL = """{
  "__typeName": "OpenURLAction",
  "url": "https://www.example.com/",
  "dismissExperience": false
}"""

    const val action_PerformSegue_push = """{
  "__typeName": "PerformSegueAction",
  "screenID": "EB91936F-C2AA-4847-BF26-A602AD12B2CA",
  "segueStyle": "push"
}"""

    const val action_PerformSegue_modal = """{
  "__typeName": "PerformSegueAction",
  "screenID": "EB91936F-C2AA-4847-BF26-A602AD12B2CA",
  "segueStyle": "modal"
}"""

    const val action_Custom = """{
  "__typeName": "CustomAction",
  "dismissExperience": true
}"""

    const val border = """{
    "color": {
        "default": {
            "red": 0.1,
            "green": 0.2,
            "blue": 0.3,
            "alpha": 0.4
        },
        "darkMode": {
            "red": 0.4,
            "green": 0.3,
            "blue": 0.2,
            "alpha": 0.1
        }
    },
    "width": 3
}"""

    const val fill_flat = """{
                    "__typeName": "FlatFill",
                    "color": {
                        "id": "606D373F-F6C3-4E24-B663-297CB4AB3D56",
                        "default": {
                            "red": 1,
                            "alpha": 1,
                            "blue": 1,
                            "green": 1
                        },
                        "darkMode": {
                            "red": 0.10196078431372549,
                            "alpha": 1,
                            "blue": 0.23529411764705882,
                            "green": 0.10980392156862742
                        },
                        "name": "Background Color"
                    }
                }"""

    const val gradientVariant = """{
                        "default": {
                            "to": [
                                0.5,
                                1
                            ],
                            "stops": [
                                {
                                    "color": {
                                        "red": 1,
                                        "alpha": 0,
                                        "blue": 1,
                                        "green": 1
                                    },
                                    "position": 0.2389179747781636
                                },
                                {
                                    "color": {
                                        "red": 1,
                                        "alpha": 1,
                                        "blue": 1,
                                        "green": 1
                                    },
                                    "position": 1
                                }
                            ],
                            "from": [
                                0.5,
                                0
                            ]
                        },
                        "darkMode": {
                            "to": [
                                0.5,
                                1
                            ],
                            "stops": [
                                {
                                    "color": {
                                        "red": 0.10196078431372549,
                                        "alpha": 0,
                                        "blue": 0.23529411764705882,
                                        "green": 0.10980392156862742
                                    },
                                    "position": 0
                                },
                                {
                                    "color": {
                                        "red": 0.10196078431372549,
                                        "alpha": 1,
                                        "blue": 0.23529411764705882,
                                        "green": 0.10980392156862742
                                    },
                                    "position": 1
                                }
                            ],
                            "from": [
                                0.5,
                                0
                            ]
                        }
                    }"""

    const val font_fixed: String = """
        {
    "__typeName": "FixedFont",
    "weight": "regular",
    "size": 16,
    "isDynamic": false
}"""

    const val resource_font_single: String = """{
    "__typeName": "FontResource",
    "url": "https://content.judo.app/fonts/iwqmmerhbg32mh1.ttf",
    "fontName": "AvenirNext"
}"""

    const val resource_font_family: String = """{
    "__typeName": "FontCollectionResource",
    "url": "https://content.judo.app/fonts/grwqwl3l299j.ttc",
    "fontNames": ["AvenirNext", "AvenirNext-Bold", "AvenirNext-Thin"]
}"""

    const val font_dynamic: String = """{
    "__typeName": "DynamicFont",
    "textStyle": "title1",
    "isDynamic": true,
    "emphases": []
}"""

    const val font_custom: String = """{
    "__typeName": "CustomFont",
    "fontName": "body",
    "size": 16,
    "isDynamic": true
}   """

    const val localization: String = """{
        "fr": {
            "Hello, world!": "Salut tout le monde!"
        }
    }"""

    const val statusBarAppearance: String = """{
    "style": "default",
    "androidStyle": "default",
    "backgroundColor": {
        "default": {
            "red": 1,
            "blue": 1,
            "green": 0,
            "alpha": 0.1
        }
    }
}"""

    const val text: String = """{
    "id": "799AAF28-9ABB-476F-BD9D-1E2BA785FB4B",
    "__typeName": "Text",
    "textAlignment": "leading",
    "text": "Hello World",
    "font": {
        "__typeName": "FixedFont",
        "weight": "regular",
        "size": 16,
        "isDynamic": false
    },
    "textColor": {
        "default": {
            "red": 1,
            "green": 1,
            "blue": 0.5,
            "alpha": 0.1
        }
    },
    "lineLimit": 3,
    "transform": "uppercase"
}"""

    const val carousel: String = """    {
      "id": "EE1A3941-E57D-47AC-BDA6-7979824B2708",
      "childIDs": [
        "02ADE676-1ECE-4747-90DA-4055AE1DC0BF",
        "A19187AD-5898-431F-B097-680C06A728B0",
        "A3CEC381-0091-4B4C-B9E6-2BDC814AF90F"
      ],
      "frame": {
        "alignment": "center",
        "height": 224
      },
      "isLoopEnabled": false,
      "name": "Carousel",
      "__typeName": "Carousel",
      "overlay": {
        "node": {
             "id": "71D699A6-B059-4F0C-91AE-59ADD341C0DE",
        "childIDs": [],
        "fill": {
          "__typeName": "GradientFill",
          "gradient": {
            "default": {
              "to": [
                0.5,
                1
              ],
              "stops": [
                {
                  "color": {
                    "red": 1,
                    "alpha": 0,
                    "blue": 1,
                    "green": 1
                  },
                  "position": 0.2389179747781636
                },
                {
                  "color": {
                    "red": 1,
                    "alpha": 1,
                    "blue": 1,
                    "green": 1
                  },
                  "position": 1
                }
              ],
              "from": [
                0.5,
                0
              ]
            },
            "darkMode": {
              "to": [
                0.5,
                1
              ],
              "stops": [
                {
                  "color": {
                    "red": 0.10196078431372549,
                    "alpha": 0,
                    "blue": 0.23529411764705882,
                    "green": 0.10980392156862742
                  },
                  "position": 0
                },
                {
                  "color": {
                    "red": 0.10196078431372549,
                    "alpha": 1,
                    "blue": 0.23529411764705882,
                    "green": 0.10980392156862742
                  },
                  "position": 1
                }
              ],
              "from": [
                0.5,
                0
              ]
            }
          }
        },
        "cornerRadius": 0,
        "name": "",
        "__typeName": "Rectangle"
        },
        "alignment": "center"
      }
    }"""

    const val frame = """{
    "minWidth": 50,
    "maxWidth": "inf",
    "minHeight": 100,
    "maxHeight": 150,
    "alignment": "top"
}"""

    const val webView = """{
    "id": "799AAF28-9ABB-476F-BD9D-1E2BA785FB4B",
    "__typeName": "WebView",
    "url": "https://www.rover.io",
    "isScrollEnabled": false
}"""

    val data_source = """{
  "id": "0F79A117-D509-4C50-95D2-9F23A3373C54",
  "__typeName": "DataSource",
  "childIDs": [
    "8F94C70E-7C09-4711-B808-C7E55BDCFE8D"
  ],
  "name": "Games List",
  "headers": [
    {
      "key": "Content-Type",
      "value": "application/json"
    },
    {
      "key": "x-api-key",
      "value": "da2-wxvphvq7g5hipblomav5dqmgye"
    }
  ],
  "httpMethod": "POST",
  "url": "https://uapi.fanreachdata.io/graphql",
  "httpBody": "{\n  \"variables\": {\n    \"leagueCode\": \"MLS\",\n    \"teamCode\": \"ATL\",\n    \"decorationsForPersonaLookupKey\": \"atlanta_united\",\n    \"withDecorations\": true\n  },\n  \"query\": \"query( ${'$'}leagueCode: String!, ${'$'}teamCode: String!, ${'$'}withDecorations: Boolean!, ${'$'}decorationsForPersonaLookupKey: String!) {\\n  listGames(leagueCode: ${'$'}leagueCode, filter: {teamCodes: {contains: ${'$'}teamCode}}) {\\n    items {\\n        ...GameFragment\\n    }\\n    nextToken\\n  }\\n}\\n\\nfragment GameFragment on Game {\\n  id\\n  analyticsId\\n  analyticsTitle\\n  statId\\n  seasonType\\n  gameType\\n  startsAt\\n  clock\\n  phase\\n  isFinal\\n  isPostponed\\n  status\\n  venueName\\n  venueLocation\\n  broadcastChannels\\n  homeTeamCode\\n  homeTeamId\\n  visitorTeamCode\\n  visitorTeamId\\n  teamOneCode\\n  teamOneId\\n  teamOneName\\n  teamOneScore\\n  teamOneRecord\\n  teamOneLogoUrl\\n  teamTwoCode\\n  teamTwoId\\n  teamTwoName\\n  teamTwoScore\\n  teamTwoRecord\\n  teamTwoLogoUrl\\n  winningTeamId\\n  winningTeamCode\\n  webUrl\\n  ticketingUrl\\n  deeplinkUrl\\n  leagueCode\\n  decorations(personaLookupKey: {eq: ${'$'}decorationsForPersonaLookupKey}) @include(if: ${'$'}withDecorations) {\\n    items {\\n        teamCode\\n        personaLookupKey\\n        sponsor {\\n            imageUrl\\n            name\\n        }\\n    }\\n  }\\n}\"\n}"
}""".trimMargin()

    val collection = """{
            "id": "5FC3CE16-6BF6-4214-92C7-5F6FBB067409",
            "__typeName": "app.judo.sdk.api.models.Collection",
            "childIDs": [
                "53F6CBB6-050C-492C-A854-D01C9F5A1323"
            ],
            "name": "app.judo.sdk.api.models.Collection",
            "dataKey": "data",
            "filters": [],
            "sortDescriptors": []
        }""".trimMargin()

    const val register_response = """{
    "appId": 9,
    "deviceToken": "aToken",
    "isProduction": false
}"""

    const val app_bar = """{
  "showUpArrow": true,
  "upArrowIconURL": "https://storage.judo.app/up-arrrow",
  "iconColor": {
    "default": {
      "alpha": 0.0,
      "blue": 0.0,
      "green": 0.0,
      "red": 0.0
    }
  },
  "title": "AppBar!!",
  "titleFont": {
    "__typeName": "FixedFont",
    "weight": "bold",
    "size": 20.0,
    "isDynamic": false
  },
  "titleColor": {
    "default": {
      "alpha": 0.0,
      "blue": 0.0,
      "green": 0.0,
      "red": 0.0
    }
  },
  "backgroundColor": {
    "default": {
      "alpha": 0.0,
      "blue": 0.0,
      "green": 0.0,
      "red": 0.0
    }
  },
  "menuItems": [
    {
      "title": "Home",
      "titleFont": {
    "__typeName": "FixedFont",
    "weight": "regular",
    "size": 12.0,
    "isDynamic": true
  },
  "titleColor": {
    "default": {
      "alpha": 1.0,
      "blue": 1.0,
      "green": 1.0,
      "red": 1.0
    }
  },
      "action": {
        "__typeName": "CloseAction"
      },
      "menuItemVisibility": "always",
      "icon": {
        "__typeName": "MenuItemIcon",
        "icon": {
          "materialName": "house"
        }
      },
      "contentDescription": "Home",
      "actionDescription": "Go Home"
    }
  ]
}"""

    private const val dummyBlurHash = "UQF5\${'$'}|xu9#Sh~7xsJ=NHW9jDoeI^OHWA\$eIp"
    val experience = """{
  "id": "3",
  "version": 1,
  "revisionID": 3,
  "screenIDs": [
    "3B19D34C-F859-42E6-90CE-0576DF70C13E"
  ],
  "appearance": "light",
  "nodes": [
    {
      "backgroundColor": {
        "systemName": "systemBackground",
        "default": {
          "red": 1,
          "green": 1,
          "blue": 1,
          "alpha": 1
        },
        "darkMode": {
          "red": 0,
          "green": 0,
          "blue": 0,
          "alpha": 1
        }
      },
      "navBar": {
        "backButtonDisplayMode": "default",
        "title": "",
        "titleDisplayMode": "normal",
        "trailingButtonItem": {
          "__typeName": "DoneButtonItem"
        }
      },
      "name": "Screen",
      "id": "3B19D34C-F859-42E6-90CE-0576DF70C13E",
      "childIDs": [
        "7DEADBAB-15B8-4333-BC68-6156F74E5A26"
      ],
      "modalPresentationStyle": "sheet",
      "__typeName": "Screen",
      "statusBarStyle": "default"
    },
    {
      "id": "7DEADBAB-15B8-4333-BC68-6156F74E5A26",
      "disableScrollBar": false,
      "childIDs": [
        "246B9FC8-D624-40C6-8684-A4C3BF437719"
      ],
      "axis": "vertical",
      "name": "Scroll Container",
      "__typeName": "ScrollContainer"
    },
    {
      "id": "246B9FC8-D624-40C6-8684-A4C3BF437719",
      "childIDs": [
        "8D32260B-1E67-411A-9ABD-283905DAD9CD",
        "D3DE29D4-2A91-4403-8A3E-50ED15240238",
        "722B3D71-F95E-4583-9E62-4AD859B149AB",
        "3D0F26B1-8AAB-4E35-BD87-D80B4C04328D"
      ],
      "spacing": 32,
      "alignment": "center",
      "padding": {
        "bottom": 16,
        "leading": 16,
        "top": 16,
        "trailing": 16
      },
      "name": "VStack 3",
      "__typeName": "VStack"
    },
    {
      "id": "8D32260B-1E67-411A-9ABD-283905DAD9CD",
      "childIDs": [
        "EE072783-C222-4630-B112-69A9E1DDF8D2",
        "F957538D-5360-4A1C-ADF3-082D687CC1F7"
      ],
      "alignment": "top",
      "padding": {
        "bottom": 24,
        "leading": 0,
        "top": 24,
        "trailing": 0
      },
      "name": "Header",
      "__typeName": "ZStack"
    },
    {
      "id": "EE072783-C222-4630-B112-69A9E1DDF8D2",
      "padding": {
        "bottom": 4,
        "leading": 16,
        "top": 4,
        "trailing": 16
      },
      "font": {
        "__typeName": "CustomFont",
        "fontName": "Knockout-HTF48-Featherweight",
        "size": 17,
        "isDynamic": true
      },
      "textAlignment": "center",
      "__typeName": "Text",
      "textColor": {
        "systemName": "systemBackground",
        "default": {
          "red": 1,
          "green": 1,
          "blue": 1,
          "alpha": 1
        },
        "darkMode": {
          "red": 0,
          "green": 0,
          "blue": 0,
          "alpha": 1
        }
      },
      "metadata": {
        "properties": {
          "firstkey": "firstvalue",
          "secondkey": "secondvalue"
        },
        "tags": [
          "tag1",
          "tag2"
        ]
      },
      "transform": "uppercase",
      "childIDs": [],
      "background": {
        "node": {
          "id": "69A75334-CB70-4A4F-BDA3-278BE6A9A0A4",
          "childIDs": [],
          "fill": {
            "__typeName": "FlatFill",
            "color": {
              "systemName": "label",
              "default": {
                "red": 0,
                "green": 0,
                "blue": 0,
                "alpha": 1
              },
              "darkMode": {
                "red": 1,
                "green": 1,
                "blue": 1,
                "alpha": 1
              }
            }
          },
          "cornerRadius": 0,
          "name": "",
          "__typeName": "Rectangle"
        },
        "alignment": "center"
      },
      "offset": [
        0,
        -20
      ],
      "text": "Thursday, December 12, 2020\n7:00 PM CT",
      "name": " Thursday, December"
    },
    {
      "id": "F957538D-5360-4A1C-ADF3-082D687CC1F7",
      "childIDs": [
        "5778E76F-EE20-4D5D-8252-462831D42726",
        "D4FEBA66-3140-4704-8EBD-037C2CADF246"
      ],
      "spacing": 16,
      "alignment": "center",
      "name": "HStack",
      "__typeName": "HStack"
    },
    {
      "id": "5778E76F-EE20-4D5D-8252-462831D42726",
      "childIDs": [
        "25F6E436-C82D-48A8-8FF5-A535D51DCE91"
      ],
      "alignment": "center",
      "padding": {
        "bottom": 16,
        "leading": 16,
        "top": 16,
        "trailing": 16
      },
      "background": {
        "node": {
          "id": "A79FB0D7-C787-46BD-AF82-4814D253B87D",
          "childIDs": [],
          "fill": {
            "__typeName": "FlatFill",
            "color": {
              "systemName": "secondarySystemBackground",
              "default": {
                "red": 0.9490196078431372,
                "green": 0.9490196078431372,
                "blue": 0.9686274509803922,
                "alpha": 1
              },
              "darkMode": {
                "red": 0.10980392156862745,
                "green": 0.10980392156862745,
                "blue": 0.11764705882352941,
                "alpha": 1
              }
            }
          },
          "cornerRadius": 4,
          "name": "",
          "__typeName": "Rectangle"
        },
        "alignment": "center"
      },
      "name": "ZStack",
      "__typeName": "ZStack"
    },
    {
      "id": "25F6E436-C82D-48A8-8FF5-A535D51DCE91",
      "childIDs": [
        "FE3B10D7-0580-48E0-A101-DAE9C01C7D50",
        "8CBF04AF-8B47-4610-86B3-BFB0171455BE"
      ],
      "spacing": 8,
      "alignment": "center",
      "name": "VStack",
      "__typeName": "VStack"
    },
    {
      "resolution": 2,
      "id": "FE3B10D7-0580-48E0-A101-DAE9C01C7D50",
      "childIDs": [],
      "resizingMode": "scaleToFit",
      "frame": {
        "alignment": "center",
        "height": 80,
        "width": 80
      },
      "imageURL": "https://content.judo.app/images/5bb45475aeb2d39fffe523795268d45295a7de9cbef42d3ac1caea39ca124390.png",
      "imageHeight": 105,
      "imageWidth": 105,
      "blurHash": "$dummyBlurHash",
      "name": "ofjScRGiytT__Flak2j4dg_96x96",
      "__typeName": "Image"
    },
    {
      "name": " 3-0",
      "textAlignment": "leading",
      "id": "8CBF04AF-8B47-4610-86B3-BFB0171455BE",
      "childIDs": [],
      "text": "3-0",
      "font": {
        "__typeName": "CustomFont",
        "fontName": "Knockout-HTF48-Featherweight",
        "size": 34,
        "isDynamic": true
      },
      "textColor": {
        "systemName": "label",
        "default": {
          "red": 0,
          "green": 0,
          "blue": 0,
          "alpha": 1
        },
        "darkMode": {
          "red": 1,
          "green": 1,
          "blue": 1,
          "alpha": 1
        }
      },
      "__typeName": "Text"
    },
    {
      "id": "D4FEBA66-3140-4704-8EBD-037C2CADF246",
      "childIDs": [
        "5D6B809A-4A08-4621-A056-A6A4879D4BA9"
      ],
      "alignment": "center",
      "padding": {
        "bottom": 16,
        "leading": 16,
        "top": 16,
        "trailing": 16
      },
      "metadata": {
        "properties": {
          "firstkey": "firstvalue",
          "secondkey": "secondvalue"
        },
        "tags": [
          "tag1",
          "tag2"
        ]
      },
      "background": {
        "node": {
          "id": "B09256D0-B4AF-4CE3-B7DC-21FF4C293C5C",
          "childIDs": [],
          "fill": {
            "__typeName": "FlatFill",
            "color": {
              "systemName": "secondarySystemBackground",
              "default": {
                "red": 0.9490196078431372,
                "green": 0.9490196078431372,
                "blue": 0.9686274509803922,
                "alpha": 1
              },
              "darkMode": {
                "red": 0.10980392156862745,
                "green": 0.10980392156862745,
                "blue": 0.11764705882352941,
                "alpha": 1
              }
            }
          },
          "cornerRadius": 4,
          "name": "",
          "__typeName": "Rectangle"
        },
        "alignment": "center"
      },
      "name": "ZStack 2",
      "__typeName": "ZStack"
    },
    {
      "id": "5D6B809A-4A08-4621-A056-A6A4879D4BA9",
      "childIDs": [
        "7F79D6B3-CE72-468D-A667-D7EB06AE94B0",
        "F9F14FF2-54F9-4D0A-87E4-466BD34D0CD5"
      ],
      "spacing": 8,
      "alignment": "center",
      "name": "VStack 2",
      "__typeName": "VStack"
    },
    {
      "resolution": 2,
      "id": "7F79D6B3-CE72-468D-A667-D7EB06AE94B0",
      "childIDs": [],
      "resizingMode": "scaleToFit",
      "frame": {
        "alignment": "center",
        "height": 80,
        "width": 80
      },
      "imageURL": "https://content.judo.app/images/26bf0c721a51069cde1b8f45ecc100326eb5fb9e968c57c90f34a3f612d12f44.png",
      "imageHeight": 161,
      "imageWidth": 110,
      "blurHash": "UAEnbP${'$'}*;3xZpeX8rXoM{_bHsW${'$'}%zUsn}tbH",
      "name": "cavs logo",
      "__typeName": "Image"
    },
    {
      "name": " 3-0",
      "textAlignment": "leading",
      "id": "F9F14FF2-54F9-4D0A-87E4-466BD34D0CD5",
      "childIDs": [],
      "text": "3-0",
      "font": {
        "__typeName": "CustomFont",
        "fontName": "Knockout-HTF48-Featherweight",
        "size": 34,
        "isDynamic": true
      },
      "textColor": {
        "systemName": "label",
        "default": {
          "red": 0,
          "green": 0,
          "blue": 0,
          "alpha": 1
        },
        "darkMode": {
          "red": 1,
          "green": 1,
          "blue": 1,
          "alpha": 1
        }
      },
      "__typeName": "Text"
    },
    {
      "id": "D3DE29D4-2A91-4403-8A3E-50ED15240238",
      "childIDs": [
        "C9831631-2E41-40C7-BE61-DBD943E9F343",
        "7FCC470B-A957-4FBF-9C0D-74412C243886"
      ],
      "spacing": 8,
      "alignment": "center",
      "name": "VStack 4",
      "__typeName": "VStack"
    },
    {
      "id": "C9831631-2E41-40C7-BE61-DBD943E9F343",
      "childIDs": [
        "EDCAE8C3-4EB1-476E-B4D7-C152A3DE5917",
        "4D6EE08E-317F-4C0A-9583-F6915B7D0005"
      ],
      "spacing": 8,
      "alignment": "center",
      "name": "Featured Stories",
      "__typeName": "HStack"
    },
    {
      "__typeName": "Text",
      "textAlignment": "leading",
      "name": " Featured Stories",
      "id": "EDCAE8C3-4EB1-476E-B4D7-C152A3DE5917",
      "childIDs": [],
      "text": "Featured Stories",
      "font": {
        "__typeName": "CustomFont",
        "fontName": "Knockout-HTF48-Featherweight",
        "size": 22,
        "isDynamic": true
      },
      "textColor": {
        "systemName": "label",
        "default": {
          "red": 0,
          "green": 0,
          "blue": 0,
          "alpha": 1
        },
        "darkMode": {
          "red": 1,
          "green": 1,
          "blue": 1,
          "alpha": 1
        }
      },
      "transform": "uppercase"
    },
    {
      "__typeName": "Spacer",
      "id": "4D6EE08E-317F-4C0A-9583-F6915B7D0005",
      "childIDs": [],
      "name": "Spacer"
    },
    {
      "id": "7FCC470B-A957-4FBF-9C0D-74412C243886",
      "disableScrollBar": true,
      "childIDs": [
        "775BA8F2-F59E-4238-80CF-C0CC34561119"
      ],
      "axis": "horizontal",
      "name": "Scroll Container 2",
      "__typeName": "ScrollContainer"
    },
    {
      "id": "775BA8F2-F59E-4238-80CF-C0CC34561119",
      "childIDs": [
        "28060161-94B3-433D-85B5-B5A9DE917DAC",
        "99C18DDA-B190-4B09-8A29-1BC3434BAEF6",
        "7D1EA1DD-7282-4649-A455-65F5C49F548D",
        "E55458F5-B41C-4A64-8340-88ED590B2869",
        "8A618F92-1FBC-42EC-BE8F-11AC44DA3769"
      ],
      "spacing": 16,
      "alignment": "center",
      "name": "HStack 2",
      "__typeName": "HStack"
    },
    {
      "action": {
        "__typeName": "PerformSegueAction",
        "screenID": "8289DF8C-0AE2-464E-9775-96FB0E876263",
        "segueStyle": "push"
      },
      "id": "28060161-94B3-433D-85B5-B5A9DE917DAC",
      "childIDs": [
        "014483AB-EB82-4127-BF49-B9E66EC3500D",
        "FCCC3C60-65D3-4178-9453-969747A7A769"
      ],
      "alignment": "topTrailing",
      "name": "Item",
      "__typeName": "ZStack"
    },
    {
      "id": "014483AB-EB82-4127-BF49-B9E66EC3500D",
      "padding": {
        "bottom": 4,
        "leading": 8,
        "top": 4,
        "trailing": 8
      },
      "font": {
        "__typeName": "FixedFont",
        "size": 9,
        "weight": "semibold",
        "isDynamic": false
      },
      "textAlignment": "leading",
      "__typeName": "Text",
      "textColor": {
        "systemName": "white",
        "default": {
          "red": 1,
          "green": 1,
          "blue": 1,
          "alpha": 1
        },
        "darkMode": {
          "red": 1,
          "green": 1,
          "blue": 1,
          "alpha": 1
        },
        "highContrast": {
          "red": 1,
          "green": 1,
          "blue": 1,
          "alpha": 1
        },
        "darkModeHighContrast": {
          "red": 1,
          "green": 1,
          "blue": 1,
          "alpha": 1
        }
      },
      "transform": "uppercase",
      "childIDs": [],
      "background": {
        "node": {
          "id": "F0309E9E-A748-4151-84DF-5F998AA6B0E1",
          "childIDs": [],
          "fill": {
            "__typeName": "FlatFill",
            "color": {
              "id": "9150BC58-A9B8-412E-82E3-9434284C3CF2",
              "name": "Bulls Red",
              "default": {
                "red": 0.7294117647058823,
                "alpha": 1,
                "blue": 0.20784313725490208,
                "green": 0.043137254901960735
              }
            }
          },
          "cornerRadius": 26,
          "name": "",
          "__typeName": "Rectangle"
        },
        "alignment": "center"
      },
      "offset": [
        -8,
        8
      ],
      "text": "New",
      "name": " New"
    },
    {
      "__typeName": "ZStack",
      "alignment": "bottomLeading",
      "id": "FCCC3C60-65D3-4178-9453-969747A7A769",
      "childIDs": [
        "B3F2ACB5-2741-4F0E-A5EB-AE11DB1C0E70",
        "ECC483BB-55B1-4B0D-8567-46941E7F321C"
      ],
      "name": "ZStack 3"
    },
    {
      "id": "B3F2ACB5-2741-4F0E-A5EB-AE11DB1C0E70",
      "padding": {
        "bottom": 8,
        "leading": 8,
        "top": 8,
        "trailing": 8
      },
      "font": {
        "__typeName": "DynamicFont",
        "textStyle": "caption2",
        "isDynamic": true,
        "emphases": []
      },
      "textAlignment": "leading",
      "__typeName": "Text",
      "textColor": {
        "systemName": "label",
        "default": {
          "red": 0,
          "green": 0,
          "blue": 0,
          "alpha": 1
        },
        "darkMode": {
          "red": 1,
          "green": 1,
          "blue": 1,
          "alpha": 1
        }
      },
      "transform": "uppercase",
      "childIDs": [],
      "text": "Shootaround from Advocate Center",
      "layoutPriority": -1,
      "name": " Shootaround from"
    },
    {
      "id": "ECC483BB-55B1-4B0D-8567-46941E7F321C",
      "childIDs": [],
      "frame": {
        "alignment": "center",
        "height": 156,
        "width": 112
      },
      "fill": {
        "__typeName": "FlatFill",
        "color": {
          "systemName": "systemFill",
          "default": {
            "red": 0.47058823529411764,
            "green": 0.47058823529411764,
            "blue": 0.5019607843137255,
            "alpha": 0.2
          },
          "darkMode": {
            "red": 0.47058823529411764,
            "green": 0.47058823529411764,
            "blue": 0.5019607843137255,
            "alpha": 0.36
          }
        }
      },
      "cornerRadius": 8,
      "name": "Rectangle",
      "__typeName": "Rectangle"
    },
    {
      "action": {
        "__typeName": "PerformSegueAction",
        "screenID": "8289DF8C-0AE2-464E-9775-96FB0E876263",
        "segueStyle": "push"
      },
      "id": "99C18DDA-B190-4B09-8A29-1BC3434BAEF6",
      "childIDs": [
        "9892B3C5-6968-4996-AB95-B43BC79572B2",
        "6898F4D7-ACEC-4203-ABC7-EAA6FD9FDDA7"
      ],
      "alignment": "topTrailing",
      "name": "Item",
      "__typeName": "ZStack"
    },
    {
      "id": "9892B3C5-6968-4996-AB95-B43BC79572B2",
      "padding": {
        "bottom": 4,
        "leading": 8,
        "top": 4,
        "trailing": 8
      },
      "font": {
        "__typeName": "FixedFont",
        "size": 9,
        "weight": "semibold",
        "isDynamic": false
      },
      "textAlignment": "leading",
      "__typeName": "Text",
      "textColor": {
        "systemName": "white",
        "default": {
          "red": 1,
          "green": 1,
          "blue": 1,
          "alpha": 1
        },
        "darkMode": {
          "red": 1,
          "green": 1,
          "blue": 1,
          "alpha": 1
        },
        "highContrast": {
          "red": 1,
          "green": 1,
          "blue": 1,
          "alpha": 1
        },
        "darkModeHighContrast": {
          "red": 1,
          "green": 1,
          "blue": 1,
          "alpha": 1
        }
      },
      "transform": "uppercase",
      "childIDs": [],
      "background": {
        "node": {
          "id": "64E615FC-24B8-4C3E-9526-FE74C60B6CEE",
          "childIDs": [],
          "fill": {
            "__typeName": "FlatFill",
            "color": {
              "id": "9150BC58-A9B8-412E-82E3-9434284C3CF2",
              "name": "Bulls Red",
              "default": {
                "red": 0.7294117647058823,
                "alpha": 1,
                "blue": 0.20784313725490208,
                "green": 0.043137254901960735
              }
            }
          },
          "cornerRadius": 26,
          "name": "",
          "__typeName": "Rectangle"
        },
        "alignment": "center"
      },
      "offset": [
        -8,
        8
      ],
      "text": "New",
      "name": " New"
    },
    {
      "__typeName": "ZStack",
      "alignment": "bottomLeading",
      "id": "6898F4D7-ACEC-4203-ABC7-EAA6FD9FDDA7",
      "childIDs": [
        "8996BD8C-45B3-441A-B220-1FD45483F03A",
        "91705A5B-25D4-40B5-8A38-0CEAE8596E89"
      ],
      "name": "ZStack 3"
    },
    {
      "id": "8996BD8C-45B3-441A-B220-1FD45483F03A",
      "padding": {
        "bottom": 8,
        "leading": 8,
        "top": 8,
        "trailing": 8
      },
      "font": {
        "__typeName": "DynamicFont",
        "textStyle": "caption2",
        "isDynamic": true,
        "emphases": ["bold"]
      },
      "textAlignment": "leading",
      "__typeName": "Text",
      "textColor": {
        "systemName": "label",
        "default": {
          "red": 0,
          "green": 0,
          "blue": 0,
          "alpha": 1
        },
        "darkMode": {
          "red": 1,
          "green": 1,
          "blue": 1,
          "alpha": 1
        }
      },
      "transform": "uppercase",
      "childIDs": [],
      "text": "Pregame\nInterviews",
      "layoutPriority": -1,
      "name": " Pregame\nInterviews"
    },
    {
      "id": "91705A5B-25D4-40B5-8A38-0CEAE8596E89",
      "childIDs": [],
      "frame": {
        "alignment": "center",
        "height": 156,
        "width": 112
      },
      "fill": {
        "__typeName": "FlatFill",
        "color": {
          "systemName": "systemFill",
          "default": {
            "red": 0.47058823529411764,
            "green": 0.47058823529411764,
            "blue": 0.5019607843137255,
            "alpha": 0.2
          },
          "darkMode": {
            "red": 0.47058823529411764,
            "green": 0.47058823529411764,
            "blue": 0.5019607843137255,
            "alpha": 0.36
          }
        }
      },
      "cornerRadius": 8,
      "name": "Rectangle",
      "__typeName": "Rectangle"
    },
    {
      "action": {
        "__typeName": "PerformSegueAction",
        "screenID": "8289DF8C-0AE2-464E-9775-96FB0E876263",
        "segueStyle": "push"
      },
      "id": "7D1EA1DD-7282-4649-A455-65F5C49F548D",
      "childIDs": [
        "E58FE5DE-73F0-4146-BF74-CA851187114B",
        "7A9C6216-18C8-4A48-9BAA-FA836280ED1F"
      ],
      "alignment": "topTrailing",
      "name": "Item",
      "__typeName": "ZStack"
    },
    {
      "id": "E58FE5DE-73F0-4146-BF74-CA851187114B",
      "padding": {
        "bottom": 4,
        "leading": 8,
        "top": 4,
        "trailing": 8
      },
      "font": {
        "__typeName": "FixedFont",
        "size": 9,
        "weight": "semibold",
        "isDynamic": false
      },
      "textAlignment": "leading",
      "__typeName": "Text",
      "textColor": {
        "systemName": "white",
        "default": {
          "red": 1,
          "green": 1,
          "blue": 1,
          "alpha": 1
        },
        "darkMode": {
          "red": 1,
          "green": 1,
          "blue": 1,
          "alpha": 1
        },
        "highContrast": {
          "red": 1,
          "green": 1,
          "blue": 1,
          "alpha": 1
        },
        "darkModeHighContrast": {
          "red": 1,
          "green": 1,
          "blue": 1,
          "alpha": 1
        }
      },
      "transform": "uppercase",
      "childIDs": [],
      "background": {
        "node": {
          "id": "64E615FC-24B8-4C3E-9526-FE74C60B6CEE",
          "childIDs": [],
          "fill": {
            "__typeName": "FlatFill",
            "color": {
              "id": "9150BC58-A9B8-412E-82E3-9434284C3CF2",
              "name": "Bulls Red",
              "default": {
                "red": 0.7294117647058823,
                "alpha": 1,
                "blue": 0.20784313725490208,
                "green": 0.043137254901960735
              }
            }
          },
          "cornerRadius": 26,
          "name": "",
          "__typeName": "Rectangle"
        },
        "alignment": "center"
      },
      "offset": [
        -8,
        8
      ],
      "text": "New",
      "name": " New"
    },
    {
      "__typeName": "ZStack",
      "alignment": "bottomLeading",
      "id": "7A9C6216-18C8-4A48-9BAA-FA836280ED1F",
      "childIDs": [
        "A326E19C-51FD-4585-A9A0-3B0F9419BE83",
        "2ED321D1-FD37-4887-9E73-E1BC9FE71FFF"
      ],
      "name": "ZStack 3"
    },
    {
      "id": "A326E19C-51FD-4585-A9A0-3B0F9419BE83",
      "childIDs": [
        "188887EB-477F-40D4-AFE4-EF0689B8FD63",
        "ACAA8C91-7483-4DA9-AC9E-525DED02BBE1"
      ],
      "spacing": 8,
      "alignment": "leading",
      "padding": {
        "bottom": 8,
        "leading": 8,
        "top": 8,
        "trailing": 8
      },
      "name": "VStack 6",
      "__typeName": "VStack"
    },
    {
      "id": "188887EB-477F-40D4-AFE4-EF0689B8FD63",
      "childIDs": [
        "8B3AE30B-F511-4FDC-92CF-E949DF26571F",
        "45FE3E16-D184-43DE-A09A-7E67704850F2"
      ],
      "spacing": 4,
      "alignment": "center",
      "name": "VStack 7",
      "__typeName": "VStack"
    },
    {
      "resolution": 3,
      "id": "8B3AE30B-F511-4FDC-92CF-E949DF26571F",
      "childIDs": [],
      "resizingMode": "scaleToFit",
      "frame": {
        "alignment": "center",
        "width": 16
      },
      "imageURL": "https://content.judo.app/images/2cffe92e04748f67bbd63f109daed3457f46d8b0fba0c5496723fbeee597d037.png",
      "imageHeight": 97,
      "imageWidth": 208,
      "blurHash": "UMMr131uO?}t^+o}ofaeHXYPWBm,#8K4WVsA",
      "name": "Tissot icons",
      "__typeName": "Image"
    },
    {
      "darkModeBlurHash": "UsTSUAxufQxu~qoffQoffQfQfQfQ~qoffQof",
      "darkModeImageHeight": 121,
      "darkModeImageWidth": 698,
      "darkModeImageURL": "https://content.judo.app/images/a8622309978d8730568b6f53d23546bd2b2e54855b18402b4808d6589331f8b3.png",
      "name": "kisspng-logo-tissot-watch-brand-clock-repair-services-all-about-time-5bf40c4c1bfd63.8485023015427205881147",
      "id": "45FE3E16-D184-43DE-A09A-7E67704850F2",
      "childIDs": [],
      "resizingMode": "scaleToFit",
      "frame": {
        "alignment": "center",
        "width": 40
      },
      "imageURL": "https://content.judo.app/images/ddfd9a25b7bcefbb6b51c99329c4dc453e7d9669f0a57dcfbc9114a46780245e.png",
      "imageHeight": 121,
      "imageWidth": 698,
      "blurHash": "U009jvfQfQfQfQfQfQfQfQfQfQfQfQfQfQfQ",
      "resolution": 2,
      "__typeName": "Image"
    },
    {
      "__typeName": "Text",
      "textAlignment": "leading",
      "name": " Player\nArrivals",
      "id": "ACAA8C91-7483-4DA9-AC9E-525DED02BBE1",
      "childIDs": [],
      "text": "Player\nArrivals",
      "layoutPriority": -1,
      "font": {
        "__typeName": "DynamicFont",
        "textStyle": "caption2",
        "isDynamic": true,
        "emphases": ["italic"]
      },
      "textColor": {
        "systemName": "label",
        "default": {
          "red": 0,
          "green": 0,
          "blue": 0,
          "alpha": 1
        },
        "darkMode": {
          "red": 1,
          "green": 1,
          "blue": 1,
          "alpha": 1
        }
      },
      "transform": "uppercase"
    },
    {
      "id": "2ED321D1-FD37-4887-9E73-E1BC9FE71FFF",
      "childIDs": [],
      "frame": {
        "alignment": "center",
        "height": 156,
        "width": 112
      },
      "fill": {
        "__typeName": "FlatFill",
        "color": {
          "systemName": "systemFill",
          "default": {
            "red": 0.47058823529411764,
            "green": 0.47058823529411764,
            "blue": 0.5019607843137255,
            "alpha": 0.2
          },
          "darkMode": {
            "red": 0.47058823529411764,
            "green": 0.47058823529411764,
            "blue": 0.5019607843137255,
            "alpha": 0.36
          }
        }
      },
      "cornerRadius": 8,
      "name": "Rectangle",
      "__typeName": "Rectangle"
    },
    {
      "action": {
        "__typeName": "PerformSegueAction",
        "screenID": "8289DF8C-0AE2-464E-9775-96FB0E876263",
        "segueStyle": "push"
      },
      "id": "E55458F5-B41C-4A64-8340-88ED590B2869",
      "childIDs": [
        "45F5BC1A-3F4B-4CF7-B443-6E8BA7D8699C",
        "7095A77B-07B2-4BA3-9EA9-06C519220760"
      ],
      "alignment": "topTrailing",
      "name": "Item",
      "__typeName": "ZStack"
    },
    {
      "id": "45F5BC1A-3F4B-4CF7-B443-6E8BA7D8699C",
      "padding": {
        "bottom": 4,
        "leading": 8,
        "top": 4,
        "trailing": 8
      },
      "font": {
        "__typeName": "FixedFont",
        "size": 9,
        "weight": "semibold",
        "isDynamic": false
      },
      "textAlignment": "leading",
      "__typeName": "Text",
      "textColor": {
        "systemName": "white",
        "default": {
          "red": 1,
          "green": 1,
          "blue": 1,
          "alpha": 1
        },
        "darkMode": {
          "red": 1,
          "green": 1,
          "blue": 1,
          "alpha": 1
        },
        "highContrast": {
          "red": 1,
          "green": 1,
          "blue": 1,
          "alpha": 1
        },
        "darkModeHighContrast": {
          "red": 1,
          "green": 1,
          "blue": 1,
          "alpha": 1
        }
      },
      "transform": "uppercase",
      "childIDs": [],
      "background": {
        "node": {
          "id": "64E615FC-24B8-4C3E-9526-FE74C60B6CEE",
          "childIDs": [],
          "fill": {
            "__typeName": "FlatFill",
            "color": {
              "id": "9150BC58-A9B8-412E-82E3-9434284C3CF2",
              "name": "Bulls Red",
              "default": {
                "red": 0.7294117647058823,
                "alpha": 1,
                "blue": 0.20784313725490208,
                "green": 0.043137254901960735
              }
            }
          },
          "cornerRadius": 26,
          "name": "",
          "__typeName": "Rectangle"
        },
        "alignment": "center"
      },
      "offset": [
        -8,
        8
      ],
      "text": "New",
      "name": " New"
    },
    {
      "__typeName": "ZStack",
      "alignment": "bottomLeading",
      "id": "7095A77B-07B2-4BA3-9EA9-06C519220760",
      "childIDs": [
        "766BDCAD-26E2-40C7-A997-8BCDF659B5A0",
        "FF390CA7-568E-4851-AECA-8265ACBDBA1F"
      ],
      "name": "ZStack 3"
    },
    {
      "id": "766BDCAD-26E2-40C7-A997-8BCDF659B5A0",
      "padding": {
        "bottom": 8,
        "leading": 8,
        "top": 8,
        "trailing": 8
      },
      "font": {
        "__typeName": "DynamicFont",
        "textStyle": "caption2",
        "isDynamic": true,
        "emphases": []
      },
      "textAlignment": "leading",
      "__typeName": "Text",
      "textColor": {
        "systemName": "label",
        "default": {
          "red": 0,
          "green": 0,
          "blue": 0,
          "alpha": 1
        },
        "darkMode": {
          "red": 1,
          "green": 1,
          "blue": 1,
          "alpha": 1
        }
      },
      "transform": "uppercase",
      "childIDs": [],
      "text": "Shots of \nthe Game",
      "layoutPriority": -1,
      "name": " Shots of \nthe Game"
    },
    {
      "id": "FF390CA7-568E-4851-AECA-8265ACBDBA1F",
      "childIDs": [],
      "frame": {
        "alignment": "center",
        "height": 156,
        "width": 112
      },
      "fill": {
        "__typeName": "FlatFill",
        "color": {
          "systemName": "systemFill",
          "default": {
            "red": 0.47058823529411764,
            "green": 0.47058823529411764,
            "blue": 0.5019607843137255,
            "alpha": 0.2
          },
          "darkMode": {
            "red": 0.47058823529411764,
            "green": 0.47058823529411764,
            "blue": 0.5019607843137255,
            "alpha": 0.36
          }
        }
      },
      "cornerRadius": 8,
      "name": "Rectangle",
      "__typeName": "Rectangle"
    },
    {
      "action": {
        "__typeName": "PerformSegueAction",
        "screenID": "8289DF8C-0AE2-464E-9775-96FB0E876263",
        "segueStyle": "push"
      },
      "id": "8A618F92-1FBC-42EC-BE8F-11AC44DA3769",
      "childIDs": [
        "3EB4CF82-E392-481D-BDE0-15235DBA6C3C",
        "03A3E362-1D43-4A37-9111-06D7B4824446"
      ],
      "alignment": "topTrailing",
      "name": "Item",
      "__typeName": "ZStack"
    },
    {
      "id": "3EB4CF82-E392-481D-BDE0-15235DBA6C3C",
      "padding": {
        "bottom": 4,
        "leading": 8,
        "top": 4,
        "trailing": 8
      },
      "font": {
        "__typeName": "FixedFont",
        "size": 9,
        "weight": "semibold",
        "isDynamic": false
      },
      "textAlignment": "leading",
      "__typeName": "Text",
      "textColor": {
        "systemName": "white",
        "default": {
          "red": 1,
          "green": 1,
          "blue": 1,
          "alpha": 1
        },
        "darkMode": {
          "red": 1,
          "green": 1,
          "blue": 1,
          "alpha": 1
        },
        "highContrast": {
          "red": 1,
          "green": 1,
          "blue": 1,
          "alpha": 1
        },
        "darkModeHighContrast": {
          "red": 1,
          "green": 1,
          "blue": 1,
          "alpha": 1
        }
      },
      "transform": "uppercase",
      "childIDs": [],
      "background": {
        "node": {
          "id": "64E615FC-24B8-4C3E-9526-FE74C60B6CEE",
          "childIDs": [],
          "fill": {
            "__typeName": "FlatFill",
            "color": {
              "id": "9150BC58-A9B8-412E-82E3-9434284C3CF2",
              "name": "Bulls Red",
              "default": {
                "red": 0.7294117647058823,
                "alpha": 1,
                "blue": 0.20784313725490208,
                "green": 0.043137254901960735
              }
            }
          },
          "cornerRadius": 26,
          "name": "",
          "__typeName": "Rectangle"
        },
        "alignment": "center"
      },
      "offset": [
        -8,
        8
      ],
      "text": "New",
      "name": " New"
    },
    {
      "__typeName": "ZStack",
      "alignment": "bottomLeading",
      "id": "03A3E362-1D43-4A37-9111-06D7B4824446",
      "childIDs": [
        "C17D3F52-2989-4A21-8029-0BBFDEB02D88",
        "811E478E-7734-4DDF-8B48-5D61EDA84501"
      ],
      "name": "ZStack 3"
    },
    {
      "id": "C17D3F52-2989-4A21-8029-0BBFDEB02D88",
      "padding": {
        "bottom": 8,
        "leading": 8,
        "top": 8,
        "trailing": 8
      },
      "font": {
        "__typeName": "DynamicFont",
        "textStyle": "caption2",
        "isDynamic": true,
        "emphases": []
      },
      "textAlignment": "leading",
      "__typeName": "Text",
      "textColor": {
        "systemName": "label",
        "default": {
          "red": 0,
          "green": 0,
          "blue": 0,
          "alpha": 1
        },
        "darkMode": {
          "red": 1,
          "green": 1,
          "blue": 1,
          "alpha": 1
        }
      },
      "transform": "uppercase",
      "childIDs": [],
      "text": "Postgame\nInterviews",
      "layoutPriority": -1,
      "name": "Postgame\nInterviews…"
    },
    {
      "id": "811E478E-7734-4DDF-8B48-5D61EDA84501",
      "childIDs": [],
      "frame": {
        "alignment": "center",
        "height": 156,
        "width": 112
      },
      "fill": {
        "__typeName": "FlatFill",
        "color": {
          "systemName": "systemFill",
          "default": {
            "red": 0.47058823529411764,
            "green": 0.47058823529411764,
            "blue": 0.5019607843137255,
            "alpha": 0.2
          },
          "darkMode": {
            "red": 0.47058823529411764,
            "green": 0.47058823529411764,
            "blue": 0.5019607843137255,
            "alpha": 0.36
          }
        }
      },
      "cornerRadius": 8,
      "name": "Rectangle",
      "__typeName": "Rectangle"
    },
    {
      "id": "722B3D71-F95E-4583-9E62-4AD859B149AB",
      "childIDs": [
        "80434349-93FA-4F83-BE10-C7593FF1E9DC",
        "F5D0033F-7B60-4288-8260-58C06538C5AF"
      ],
      "spacing": 8,
      "alignment": "center",
      "name": "VStack 4",
      "__typeName": "VStack"
    },
    {
      "id": "80434349-93FA-4F83-BE10-C7593FF1E9DC",
      "childIDs": [
        "F71E902F-76E5-4101-AF5E-BD8EE02E6A63",
        "562E4362-813B-4940-8254-D62CC08C76E6"
      ],
      "spacing": 8,
      "alignment": "center",
      "name": "Featured Stories",
      "__typeName": "HStack"
    },
    {
      "__typeName": "Text",
      "textAlignment": "leading",
      "name": " Rush Sports /",
      "id": "F71E902F-76E5-4101-AF5E-BD8EE02E6A63",
      "childIDs": [],
      "text": "Rush Sports / Creative Name",
      "font": {
        "__typeName": "CustomFont",
        "fontName": "Knockout-HTF48-Featherweight",
        "size": 22,
        "isDynamic": true
      },
      "textColor": {
        "systemName": "label",
        "default": {
          "red": 0,
          "green": 0,
          "blue": 0,
          "alpha": 1
        },
        "darkMode": {
          "red": 1,
          "green": 1,
          "blue": 1,
          "alpha": 1
        }
      },
      "transform": "uppercase"
    },
    {
      "__typeName": "Spacer",
      "id": "562E4362-813B-4940-8254-D62CC08C76E6",
      "childIDs": [],
      "name": "Spacer"
    },
    {
      "id": "F5D0033F-7B60-4288-8260-58C06538C5AF",
      "childIDs": [],
      "fill": {
        "__typeName": "FlatFill",
        "color": {
          "systemName": "systemFill",
          "default": {
            "red": 0.47058823529411764,
            "green": 0.47058823529411764,
            "blue": 0.5019607843137255,
            "alpha": 0.2
          },
          "darkMode": {
            "red": 0.47058823529411764,
            "green": 0.47058823529411764,
            "blue": 0.5019607843137255,
            "alpha": 0.36
          }
        }
      },
      "cornerRadius": 8,
      "aspectRatio": 1.5,
      "name": "Rectangle 2",
      "__typeName": "Rectangle"
    },
    {
      "id": "3D0F26B1-8AAB-4E35-BD87-D80B4C04328D",
      "childIDs": [
        "BC3A94D4-16F1-43BC-84CE-A20C1D1DAA85",
        "E52A21F7-6FC8-4B7E-B2E1-297F67854C26"
      ],
      "spacing": 8,
      "alignment": "center",
      "name": "VStack 4",
      "__typeName": "VStack"
    },
    {
      "id": "BC3A94D4-16F1-43BC-84CE-A20C1D1DAA85",
      "childIDs": [
        "D4BD3A9D-EFA2-45D2-BB5D-F2B9404ED931",
        "3CE40981-2B00-481F-9F21-C1BD84208CAE"
      ],
      "spacing": 8,
      "alignment": "center",
      "name": "Featured Stories",
      "__typeName": "HStack"
    },
    {
      "__typeName": "Text",
      "textAlignment": "leading",
      "name": " Partnership",
      "id": "D4BD3A9D-EFA2-45D2-BB5D-F2B9404ED931",
      "childIDs": [],
      "text": "Partnership Activations",
      "font": {
        "__typeName": "CustomFont",
        "fontName": "Knockout-HTF48-Featherweight",
        "size": 22,
        "isDynamic": true
      },
      "textColor": {
        "systemName": "label",
        "default": {
          "red": 0,
          "green": 0,
          "blue": 0,
          "alpha": 1
        },
        "darkMode": {
          "red": 1,
          "green": 1,
          "blue": 1,
          "alpha": 1
        }
      },
      "transform": "uppercase"
    },
    {
      "__typeName": "Spacer",
      "id": "3CE40981-2B00-481F-9F21-C1BD84208CAE",
      "childIDs": [],
      "name": "Spacer"
    },
    {
      "id": "E52A21F7-6FC8-4B7E-B2E1-297F67854C26",
      "childIDs": [
        "9B24D348-4384-44FC-8DF6-8C110A091EF9",
        "7993A2E5-77FC-4657-B7CC-5FEEBFBDD88A"
      ],
      "spacing": 16,
      "alignment": "center",
      "name": "VStack 5",
      "__typeName": "VStack"
    },
    {
      "id": "9B24D348-4384-44FC-8DF6-8C110A091EF9",
      "childIDs": [
        "41F960EF-46C2-4633-9DD6-D1060790D223",
        "8CB76E7D-42A5-4FAF-8E7F-AA2C4722604C"
      ],
      "spacing": 16,
      "alignment": "center",
      "name": "HStack 3",
      "__typeName": "HStack"
    },
    {
      "id": "41F960EF-46C2-4633-9DD6-D1060790D223",
      "childIDs": [],
      "fill": {
        "__typeName": "FlatFill",
        "color": {
          "systemName": "systemFill",
          "default": {
            "red": 0.47058823529411764,
            "green": 0.47058823529411764,
            "blue": 0.5019607843137255,
            "alpha": 0.2
          },
          "darkMode": {
            "red": 0.47058823529411764,
            "green": 0.47058823529411764,
            "blue": 0.5019607843137255,
            "alpha": 0.36
          }
        }
      },
      "cornerRadius": 8,
      "aspectRatio": 1.5,
      "name": "Rectangle 2",
      "__typeName": "Rectangle"
    },
    {
      "id": "8CB76E7D-42A5-4FAF-8E7F-AA2C4722604C",
      "childIDs": [],
      "fill": {
        "__typeName": "FlatFill",
        "color": {
          "systemName": "systemFill",
          "default": {
            "red": 0.47058823529411764,
            "green": 0.47058823529411764,
            "blue": 0.5019607843137255,
            "alpha": 0.2
          },
          "darkMode": {
            "red": 0.47058823529411764,
            "green": 0.47058823529411764,
            "blue": 0.5019607843137255,
            "alpha": 0.36
          }
        }
      },
      "cornerRadius": 8,
      "aspectRatio": 1.5,
      "name": "Rectangle 2",
      "__typeName": "Rectangle"
    },
    {
      "id": "7993A2E5-77FC-4657-B7CC-5FEEBFBDD88A",
      "childIDs": [
        "E6496F52-8297-4CD0-83CD-1CD32E812839",
        "34EE7CB4-CD5B-4D39-97F3-AB8E7AAB7B9D"
      ],
      "spacing": 16,
      "alignment": "center",
      "name": "HStack 3",
      "__typeName": "HStack"
    },
    {
      "id": "E6496F52-8297-4CD0-83CD-1CD32E812839",
      "childIDs": [],
      "fill": {
        "__typeName": "FlatFill",
        "color": {
          "systemName": "systemFill",
          "default": {
            "red": 0.47058823529411764,
            "green": 0.47058823529411764,
            "blue": 0.5019607843137255,
            "alpha": 0.2
          },
          "darkMode": {
            "red": 0.47058823529411764,
            "green": 0.47058823529411764,
            "blue": 0.5019607843137255,
            "alpha": 0.36
          }
        }
      },
      "cornerRadius": 8,
      "aspectRatio": 1.5,
      "name": "Rectangle 2",
      "__typeName": "Rectangle"
    },
    {
      "id": "34EE7CB4-CD5B-4D39-97F3-AB8E7AAB7B9D",
      "childIDs": [],
      "fill": {
        "__typeName": "FlatFill",
        "color": {
          "systemName": "systemFill",
          "default": {
            "red": 0.47058823529411764,
            "green": 0.47058823529411764,
            "blue": 0.5019607843137255,
            "alpha": 0.2
          },
          "darkMode": {
            "red": 0.47058823529411764,
            "green": 0.47058823529411764,
            "blue": 0.5019607843137255,
            "alpha": 0.36
          }
        }
      },
      "cornerRadius": 8,
      "aspectRatio": 1.5,
      "name": "Rectangle 2",
      "__typeName": "Rectangle"
    },
    {
      "backgroundColor": {
        "systemName": "systemBackground",
        "default": {
          "red": 1,
          "green": 1,
          "blue": 1,
          "alpha": 1
        },
        "darkMode": {
          "red": 0,
          "green": 0,
          "blue": 0,
          "alpha": 1
        }
      },
      "navBar": {
        "backButtonDisplayMode": "hidden",
        "title": "",
        "titleDisplayMode": "normal",
        "trailingButtonItem": {
          "__typeName": "CloseButtonItem"
        }
      },
      "name": "Screen 2",
      "id": "8289DF8C-0AE2-464E-9775-96FB0E876263",
      "childIDs": [
        "998F250C-B9D3-4CFD-B452-286558FB150D"
      ],
      "navBarAppearance": {
        "largeTitleConfiguration": {
          "backgroundBlur": true,
          "backgroundColor": {
            "systemName": "clear",
            "default": {
              "red": 1,
              "green": 1,
              "blue": 1,
              "alpha": 0
            },
            "darkMode": {
              "red": 1,
              "green": 1,
              "blue": 1,
              "alpha": 0
            },
            "highContrast": {
              "red": 1,
              "green": 1,
              "blue": 1,
              "alpha": 0
            },
            "darkModeHighContrast": {
              "red": 1,
              "green": 1,
              "blue": 1,
              "alpha": 0
            }
          },
          "buttonColor": {
            "systemName": "systemBlue",
            "default": {
              "red": 0,
              "green": 0.47843137254901963,
              "blue": 1,
              "alpha": 1
            },
            "darkMode": {
              "red": 0.0392156862745098,
              "green": 0.5176470588235295,
              "blue": 1,
              "alpha": 1
            },
            "highContrast": {
              "red": 0,
              "green": 0.25098039215686274,
              "blue": 0.8666666666666667,
              "alpha": 1
            },
            "darkModeHighContrast": {
              "red": 0.25098039215686274,
              "green": 0.611764705882353,
              "blue": 1,
              "alpha": 1
            }
          },
          "buttonFont": {
            "__typeName": "DynamicFont",
            "textStyle": "body",
            "isDynamic": true,
            "emphases": []
          },
          "shadowColor": {
            "default": {
              "red": 0,
              "alpha": 0.3,
              "blue": 0,
              "green": 0
            }
          },
          "statusBarStyle": "default",
          "titleColor": {
            "systemName": "label",
            "default": {
              "red": 0,
              "green": 0,
              "blue": 0,
              "alpha": 1
            },
            "darkMode": {
              "red": 1,
              "green": 1,
              "blue": 1,
              "alpha": 1
            }
          },
          "titleFont": {
            "__typeName": "DynamicFont",
            "textStyle": "largeTitle",
            "isDynamic": true,
            "emphases": []
          }
        },
        "standardConfiguration": {
          "backgroundBlur": false,
          "backgroundColor": {
            "systemName": "clear",
            "default": {
              "red": 1,
              "green": 1,
              "blue": 1,
              "alpha": 0
            },
            "darkMode": {
              "red": 1,
              "green": 1,
              "blue": 1,
              "alpha": 0
            },
            "highContrast": {
              "red": 1,
              "green": 1,
              "blue": 1,
              "alpha": 0
            },
            "darkModeHighContrast": {
              "red": 1,
              "green": 1,
              "blue": 1,
              "alpha": 0
            }
          },
          "buttonColor": {
            "systemName": "systemBlue",
            "default": {
              "red": 0,
              "green": 0.47843137254901963,
              "blue": 1,
              "alpha": 1
            },
            "darkMode": {
              "red": 0.0392156862745098,
              "green": 0.5176470588235295,
              "blue": 1,
              "alpha": 1
            },
            "highContrast": {
              "red": 0,
              "green": 0.25098039215686274,
              "blue": 0.8666666666666667,
              "alpha": 1
            },
            "darkModeHighContrast": {
              "red": 0.25098039215686274,
              "green": 0.611764705882353,
              "blue": 1,
              "alpha": 1
            }
          },
          "buttonFont": {
            "__typeName": "DynamicFont",
            "textStyle": "body",
            "isDynamic": true,
            "emphases": []
          },
          "shadowColor": {
            "systemName": "clear",
            "default": {
              "red": 1,
              "green": 1,
              "blue": 1,
              "alpha": 0
            },
            "darkMode": {
              "red": 1,
              "green": 1,
              "blue": 1,
              "alpha": 0
            },
            "highContrast": {
              "red": 1,
              "green": 1,
              "blue": 1,
              "alpha": 0
            },
            "darkModeHighContrast": {
              "red": 1,
              "green": 1,
              "blue": 1,
              "alpha": 0
            }
          },
          "statusBarStyle": "dark",
          "titleColor": {
            "systemName": "label",
            "default": {
              "red": 0,
              "green": 0,
              "blue": 0,
              "alpha": 1
            },
            "darkMode": {
              "red": 1,
              "green": 1,
              "blue": 1,
              "alpha": 1
            }
          },
          "titleFont": {
            "__typeName": "DynamicFont",
            "textStyle": "body",
            "isDynamic": true,
            "emphases": []
          }
        }
      },
      "modalPresentationStyle": "fullScreen",
      "__typeName": "Screen",
      "statusBarStyle": "default"
    },
    {
      "id": "998F250C-B9D3-4CFD-B452-286558FB150D",
      "childIDs": [
        "15407326-7F03-4371-B4AC-3CB3246D9070",
        "FEFD27A4-A153-4EFE-86BF-C74FC0CD6F28"
      ],
      "alignment": "center",
      "ignoresSafeArea": [
        "top"
      ],
      "name": "ZStack 4",
      "__typeName": "ZStack"
    },
    {
      "id": "15407326-7F03-4371-B4AC-3CB3246D9070",
      "childIDs": [
        "6CC3D142-E18A-4E70-8566-B30459089427",
        "0BC334D1-41F8-48FA-B6FF-2032430408DE",
        "B0E62D74-E8A2-4EC9-B869-D4C9DB8AD93B"
      ],
      "spacing": 8,
      "alignment": "center",
      "name": "VStack 8",
      "__typeName": "VStack"
    },
    {
      "__typeName": "PageControl",
      "hidesForSinglePage": false,
      "name": "Page Control",
      "style": {"__typeName": "DefaultPageControlStyle"},
      "id": "6CC3D142-E18A-4E70-8566-B30459089427",
      "childIDs": [],
      "frame": {
        "alignment": "center",
        "height": 44
      },
      "offset": [
        0,
        0
      ],
      "style": {
        "__typeName": "DefaultPageControlStyle"
      },
      "carouselID": "FEFD27A4-A153-4EFE-86BF-C74FC0CD6F28"
    },
    {
      "__typeName": "Spacer",
      "id": "0BC334D1-41F8-48FA-B6FF-2032430408DE",
      "childIDs": [],
      "name": "Spacer 3"
    },
    {
      "id": "B0E62D74-E8A2-4EC9-B869-D4C9DB8AD93B",
      "childIDs": [
        "2B075355-573F-4FC2-898F-D507E208630B",
        "F3770434-41D9-482B-BB0D-559949881784"
      ],
      "spacing": 8,
      "alignment": "center",
      "background": {
        "node": {
          "id": "C9FD2F0C-2F29-499A-B005-90B2ACE18E27",
          "childIDs": [],
          "fill": {
            "__typeName": "FlatFill",
            "color": {
              "default": {
                "red": 0,
                "alpha": 0.35,
                "blue": 0,
                "green": 0
              }
            }
          },
          "cornerRadius": 0,
          "name": "",
          "__typeName": "Rectangle"
        },
        "alignment": "center"
      },
      "name": "HStack 4",
      "__typeName": "HStack"
    },
    {
      "__typeName": "Text",
      "textAlignment": "leading",
      "name": " Shootaround\nfrom",
      "id": "2B075355-573F-4FC2-898F-D507E208630B",
      "childIDs": [],
      "text": "Shootaround\nfrom Advocate\nCenter",
      "padding": {
        "bottom": 16,
        "leading": 16,
        "top": 16,
        "trailing": 16
      },
      "font": {
        "__typeName": "DynamicFont",
        "textStyle": "title2",
        "isDynamic": true,
        "emphases": []
      },
      "textColor": {
        "systemName": "white",
        "default": {
          "red": 1,
          "green": 1,
          "blue": 1,
          "alpha": 1
        },
        "darkMode": {
          "red": 1,
          "green": 1,
          "blue": 1,
          "alpha": 1
        },
        "highContrast": {
          "red": 1,
          "green": 1,
          "blue": 1,
          "alpha": 1
        },
        "darkModeHighContrast": {
          "red": 1,
          "green": 1,
          "blue": 1,
          "alpha": 1
        }
      },
      "transform": "uppercase"
    },
    {
      "__typeName": "Spacer",
      "id": "F3770434-41D9-482B-BB0D-559949881784",
      "childIDs": [],
      "name": "Spacer 2"
    },
    {
      "isLoopEnabled": true,
      "id": "FEFD27A4-A153-4EFE-86BF-C74FC0CD6F28",
      "childIDs": [
        "053442F0-778D-4117-B3F4-57CCD860EB1E",
        "27FCC088-318C-4C5E-B755-2D07E35D3833"
      ],
      "ignoresSafeArea": [
        "top"
      ],
      "name": "Carousel",
      "__typeName": "Carousel"
    },
    {
      "resolution": 1,
      "id": "053442F0-778D-4117-B3F4-57CCD860EB1E",
      "childIDs": [],
      "resizingMode": "scaleToFill",
      "imageURL": "https://content.judo.app/images/299f388f41e2f7185a2285908b72a3668c4e316d7fe6b2e5a57a712ffb411875.jpg",
      "imageHeight": 1625,
      "imageWidth": 1083,
      "blurHash": "UrM%+f%2~qWB.7bFSgx]xtWBM{of-;j[WBay",
      "name": "player1",
      "__typeName": "Image"
    },
    {
      "resolution": 1,
      "id": "27FCC088-318C-4C5E-B755-2D07E35D3833",
      "childIDs": [],
      "resizingMode": "scaleToFill",
      "imageURL": "https://content.judo.app/images/8bcc6c278903e965ba221711a68278acb274792eed750a133ff1abb0a70d998d.jpg",
      "imageHeight": 1625,
      "imageWidth": 1083,
      "blurHash": "URN0l2?v.8xa.TRPRPR*?GMxITt7~Wtlozt7",
      "name": "player2",
      "__typeName": "Image"
    }
  ],
  "rootNodeIDs": [
    "3B19D34C-F859-42E6-90CE-0576DF70C13E",
    "8289DF8C-0AE2-464E-9775-96FB0E876263"
  ],
  "initialScreenID": "3B19D34C-F859-42E6-90CE-0576DF70C13E"
}""".trimMargin()

    val nav_test_judo: String = """{
    "id": 149,
    "version": 1,
    "revisionID": 149,
    "appearance": "dark",
    "nodes": [
        {
            "id": "8B4A30F1-368A-4026-AD5D-B1F04BE1E7BF",
            "__typeName": "Screen",
            "childIDs": [
                "67976388-B142-4B21-AF39-9480FE12E5BC",
                "71324C37-2C0D-4D58-9037-0DB03FC9DEC5"
            ],
            "name": "Screen A",
            "backButtonStyle": {
                "__typeName": "DefaultBackButtonStyle",
                "title": "Screen"
            },
            "backgroundColor": {
                "systemName": "systemBackground",
                "default": {
                    "red": 1,
                    "green": 1,
                    "blue": 1,
                    "alpha": 1
                },
                "darkMode": {
                    "red": 0,
                    "green": 0,
                    "blue": 0,
                    "alpha": 1
                }
            },
            "statusBarStyle": "default"
        },
        {
            "id": "67976388-B142-4B21-AF39-9480FE12E5BC",
            "__typeName": "Text",
            "name": "Text",
            "childIDs": [],
            "text": "Close",
            "font": {
                "__typeName": "DynamicFont",
                "textStyle": "body",
                "isDynamic": true,
                "emphases": []
            },
            "textColor": {
                "systemName": "label",
                "default": {
                    "red": 0,
                    "green": 0,
                    "blue": 0,
                    "alpha": 1
                },
                "darkMode": {
                    "red": 1,
                    "green": 1,
                    "blue": 1,
                    "alpha": 1
                }
            },
            "textAlignment": "leading",
            "offset": [
                0,
                150
            ],
            "action": {
                "__typeName": "CloseAction"
            }
        },
        {
            "id": "71324C37-2C0D-4D58-9037-0DB03FC9DEC5",
            "__typeName": "Text",
            "name": " Navigate To Screen",
            "childIDs": [],
            "text": "Navigate To Screen A1",
            "font": {
                "__typeName": "DynamicFont",
                "textStyle": "body",
                "isDynamic": true,
                "emphases": []
            },
            "textColor": {
                "systemName": "label",
                "default": {
                    "red": 0,
                    "green": 0,
                    "blue": 0,
                    "alpha": 1
                },
                "darkMode": {
                    "red": 1,
                    "green": 1,
                    "blue": 1,
                    "alpha": 1
                }
            },
            "textAlignment": "leading",
            "action": {
                "__typeName": "PerformSegueAction",
                "screenID": "501241B2-2C40-489A-B6AC-0009FA7F5679",
                "segueStyle": "push"
            }
        },
        {
            "id": "501241B2-2C40-489A-B6AC-0009FA7F5679",
            "__typeName": "Screen",
            "childIDs": [
                "E41D818D-3AA2-455D-A09C-7D7F6B3A62D1",
                "5E867021-5318-4882-9B83-73452B149C71"
            ],
            "name": "Screen A1",
            "backButtonStyle": {
                "__typeName": "DefaultBackButtonStyle",
                "title": "Screen 2"
            },
            "backgroundColor": {
                "systemName": "systemBackground",
                "default": {
                    "red": 1,
                    "green": 1,
                    "blue": 1,
                    "alpha": 1
                },
                "darkMode": {
                    "red": 0,
                    "green": 0,
                    "blue": 0,
                    "alpha": 1
                }
            },
            "statusBarStyle": "default"
        },
        {
            "id": "E41D818D-3AA2-455D-A09C-7D7F6B3A62D1",
            "__typeName": "Text",
            "name": "Text",
            "childIDs": [],
            "text": "Close",
            "font": {
                "__typeName": "DynamicFont",
                "textStyle": "body",
                "isDynamic": true,
                "emphases": []
            },
            "textColor": {
                "systemName": "label",
                "default": {
                    "red": 0,
                    "green": 0,
                    "blue": 0,
                    "alpha": 1
                },
                "darkMode": {
                    "red": 1,
                    "green": 1,
                    "blue": 1,
                    "alpha": 1
                }
            },
            "textAlignment": "leading",
            "offset": [
                0,
                150
            ],
            "action": {
                "__typeName": "CloseAction"
            }
        },
        {
            "id": "5E867021-5318-4882-9B83-73452B149C71",
            "__typeName": "Text",
            "name": " Navigate To Screen",
            "childIDs": [],
            "text": "Navigate To Screen A2",
            "font": {
                "__typeName": "DynamicFont",
                "textStyle": "body",
                "isDynamic": true,
                "emphases": []
            },
            "textColor": {
                "systemName": "label",
                "default": {
                    "red": 0,
                    "green": 0,
                    "blue": 0,
                    "alpha": 1
                },
                "darkMode": {
                    "red": 1,
                    "green": 1,
                    "blue": 1,
                    "alpha": 1
                }
            },
            "textAlignment": "leading",
            "action": {
                "__typeName": "PerformSegueAction",
                "screenID": "764E30E8-E1E1-4DE4-87F1-E281BAE7B8DF",
                "segueStyle": "push"
            }
        },
        {
            "id": "764E30E8-E1E1-4DE4-87F1-E281BAE7B8DF",
            "__typeName": "Screen",
            "childIDs": [
                "483925C5-C6BE-4C26-AD36-BAB560B9BC16",
                "93B166CE-05A0-4B23-8F98-AB5709063140"
            ],
            "name": "Screen A3",
            "backButtonStyle": {
                "__typeName": "DefaultBackButtonStyle",
                "title": "Screen"
            },
            "backgroundColor": {
                "systemName": "systemBackground",
                "default": {
                    "red": 1,
                    "green": 1,
                    "blue": 1,
                    "alpha": 1
                },
                "darkMode": {
                    "red": 0,
                    "green": 0,
                    "blue": 0,
                    "alpha": 1
                }
            },
            "statusBarStyle": "default"
        },
        {
            "id": "483925C5-C6BE-4C26-AD36-BAB560B9BC16",
            "__typeName": "Text",
            "name": "Text",
            "childIDs": [],
            "text": "Close",
            "font": {
                "__typeName": "DynamicFont",
                "textStyle": "body",
                "isDynamic": true,
                "emphases": []
            },
            "textColor": {
                "systemName": "label",
                "default": {
                    "red": 0,
                    "green": 0,
                    "blue": 0,
                    "alpha": 1
                },
                "darkMode": {
                    "red": 1,
                    "green": 1,
                    "blue": 1,
                    "alpha": 1
                }
            },
            "textAlignment": "leading",
            "offset": [
                0,
                150
            ],
            "action": {
                "__typeName": "CloseAction"
            }
        },
        {
            "id": "93B166CE-05A0-4B23-8F98-AB5709063140",
            "__typeName": "Text",
            "name": " Present Screen B",
            "childIDs": [],
            "text": "Present Screen B",
            "font": {
                "__typeName": "DynamicFont",
                "textStyle": "body",
                "isDynamic": true,
                "emphases": []
            },
            "textColor": {
                "systemName": "label",
                "default": {
                    "red": 0,
                    "green": 0,
                    "blue": 0,
                    "alpha": 1
                },
                "darkMode": {
                    "red": 1,
                    "green": 1,
                    "blue": 1,
                    "alpha": 1
                }
            },
            "textAlignment": "leading",
            "action": {
                "__typeName": "PerformSegueAction",
                "screenID": "74E163E8-4D07-4570-A1A4-A580FAE48EB0",
                "segueStyle": "push"
            }
        },
        {
            "id": "74E163E8-4D07-4570-A1A4-A580FAE48EB0",
            "__typeName": "Screen",
            "childIDs": [
                "BDECE627-D2F1-465A-A9ED-42DA18295D6A",
                "4667E80E-ADBF-4E05-A5DE-149AA328952F"
            ],
            "name": "Screen B",
            "backButtonStyle": {
                "__typeName": "DefaultBackButtonStyle",
                "title": "Screen"
            },
            "backgroundColor": {
                "systemName": "systemBackground",
                "default": {
                    "red": 1,
                    "green": 1,
                    "blue": 1,
                    "alpha": 1
                },
                "darkMode": {
                    "red": 0,
                    "green": 0,
                    "blue": 0,
                    "alpha": 1
                }
            },
            "statusBarStyle": "default"
        },
        {
            "id": "BDECE627-D2F1-465A-A9ED-42DA18295D6A",
            "__typeName": "Text",
            "name": "Text",
            "childIDs": [],
            "text": "Close",
            "font": {
                "__typeName": "DynamicFont",
                "textStyle": "body",
                "isDynamic": true,
                "emphases": []
            },
            "textColor": {
                "systemName": "label",
                "default": {
                    "red": 0,
                    "green": 0,
                    "blue": 0,
                    "alpha": 1
                },
                "darkMode": {
                    "red": 1,
                    "green": 1,
                    "blue": 1,
                    "alpha": 1
                }
            },
            "textAlignment": "leading",
            "offset": [
                0,
                150
            ],
            "action": {
                "__typeName": "CloseAction"
            }
        },
        {
            "id": "4667E80E-ADBF-4E05-A5DE-149AA328952F",
            "__typeName": "Text",
            "name": " Navigate To Screen",
            "childIDs": [],
            "text": "Navigate To Screen B1",
            "font": {
                "__typeName": "DynamicFont",
                "textStyle": "body",
                "isDynamic": true,
                "emphases": []
            },
            "textColor": {
                "systemName": "label",
                "default": {
                    "red": 0,
                    "green": 0,
                    "blue": 0,
                    "alpha": 1
                },
                "darkMode": {
                    "red": 1,
                    "green": 1,
                    "blue": 1,
                    "alpha": 1
                }
            },
            "textAlignment": "leading",
            "action": {
                "__typeName": "PerformSegueAction",
                "screenID": "3FA72273-994B-4AEB-AD3C-E3966FCC43C7",
                "segueStyle": "push"
            }
        },
        {
            "id": "3FA72273-994B-4AEB-AD3C-E3966FCC43C7",
            "__typeName": "Screen",
            "childIDs": [
                "C2E19CB8-D62C-44AF-BB98-B6D30815B19B",
                "5DA6B317-2848-493F-90DE-0A8FF2A16502"
            ],
            "name": "Screen B1",
            "backButtonStyle": {
                "__typeName": "DefaultBackButtonStyle",
                "title": "Screen"
            },
            "backgroundColor": {
                "systemName": "systemBackground",
                "default": {
                    "red": 1,
                    "green": 1,
                    "blue": 1,
                    "alpha": 1
                },
                "darkMode": {
                    "red": 0,
                    "green": 0,
                    "blue": 0,
                    "alpha": 1
                }
            },
            "statusBarStyle": "default"
        },
        {
            "id": "C2E19CB8-D62C-44AF-BB98-B6D30815B19B",
            "__typeName": "Text",
            "name": "Text",
            "childIDs": [],
            "text": "Close",
            "font": {
                "__typeName": "DynamicFont",
                "textStyle": "body",
                "isDynamic": true,
                "emphases": []
            },
            "textColor": {
                "systemName": "label",
                "default": {
                    "red": 0,
                    "green": 0,
                    "blue": 0,
                    "alpha": 1
                },
                "darkMode": {
                    "red": 1,
                    "green": 1,
                    "blue": 1,
                    "alpha": 1
                }
            },
            "textAlignment": "leading",
            "offset": [
                0,
                150
            ],
            "action": {
                "__typeName": "CloseAction"
            }
        },
        {
            "id": "5DA6B317-2848-493F-90DE-0A8FF2A16502",
            "__typeName": "Text",
            "name": " Navigate To Screen",
            "childIDs": [],
            "text": "Navigate To Screen B2",
            "font": {
                "__typeName": "DynamicFont",
                "textStyle": "body",
                "isDynamic": true,
                "emphases": []
            },
            "textColor": {
                "systemName": "label",
                "default": {
                    "red": 0,
                    "green": 0,
                    "blue": 0,
                    "alpha": 1
                },
                "darkMode": {
                    "red": 1,
                    "green": 1,
                    "blue": 1,
                    "alpha": 1
                }
            },
            "textAlignment": "leading",
            "action": {
                "__typeName": "PerformSegueAction",
                "screenID": "464CE783-4046-4267-971F-C9EB2889C88A",
                "segueStyle": "push"
            }
        },
        {
            "id": "464CE783-4046-4267-971F-C9EB2889C88A",
            "__typeName": "Screen",
            "childIDs": [
                "9123591A-467D-46F9-A93B-EA663964A241",
                "E100231D-733A-419D-B843-FE6924AFCC03"
            ],
            "name": "Screen B2",
            "backButtonStyle": {
                "__typeName": "DefaultBackButtonStyle",
                "title": "Screen"
            },
            "backgroundColor": {
                "systemName": "systemBackground",
                "default": {
                    "red": 1,
                    "green": 1,
                    "blue": 1,
                    "alpha": 1
                },
                "darkMode": {
                    "red": 0,
                    "green": 0,
                    "blue": 0,
                    "alpha": 1
                }
            },
            "statusBarStyle": "default"
        },
        {
            "id": "9123591A-467D-46F9-A93B-EA663964A241",
            "__typeName": "Text",
            "name": " Navigate To Screen",
            "childIDs": [],
            "text": "Navigate To Screen C",
            "font": {
                "__typeName": "DynamicFont",
                "textStyle": "body",
                "isDynamic": true,
                "emphases": []
            },
            "textColor": {
                "systemName": "label",
                "default": {
                    "red": 0,
                    "green": 0,
                    "blue": 0,
                    "alpha": 1
                },
                "darkMode": {
                    "red": 1,
                    "green": 1,
                    "blue": 1,
                    "alpha": 1
                }
            },
            "textAlignment": "leading",
            "action": {
                "__typeName": "PerformSegueAction",
                "screenID": "3952BE18-892C-4EAD-9F16-BEA7D54FA22E",
                "segueStyle": "push"
            }
        },
        {
            "id": "E100231D-733A-419D-B843-FE6924AFCC03",
            "__typeName": "Text",
            "name": "Text",
            "childIDs": [],
            "text": "Close",
            "font": {
                "__typeName": "DynamicFont",
                "textStyle": "body",
                "isDynamic": true,
                "emphases": []
            },
            "textColor": {
                "systemName": "label",
                "default": {
                    "red": 0,
                    "green": 0,
                    "blue": 0,
                    "alpha": 1
                },
                "darkMode": {
                    "red": 1,
                    "green": 1,
                    "blue": 1,
                    "alpha": 1
                }
            },
            "textAlignment": "leading",
            "offset": [
                0,
                150
            ],
            "action": {
                "__typeName": "CloseAction"
            }
        },
        {
            "id": "3952BE18-892C-4EAD-9F16-BEA7D54FA22E",
            "__typeName": "Screen",
            "childIDs": [
                "0847239E-01D2-4C1B-AEFA-9F96769C3E5B",
                "675B4C03-3142-4219-B2F7-D224D08BFA1C"
            ],
            "name": "Screen C",
            "backButtonStyle": {
                "__typeName": "DefaultBackButtonStyle",
                "title": "Screen"
            },
            "backgroundColor": {
                "systemName": "systemBackground",
                "default": {
                    "red": 1,
                    "green": 1,
                    "blue": 1,
                    "alpha": 1
                },
                "darkMode": {
                    "red": 0,
                    "green": 0,
                    "blue": 0,
                    "alpha": 1
                }
            },
            "statusBarStyle": "default"
        },
        {
            "id": "0847239E-01D2-4C1B-AEFA-9F96769C3E5B",
            "__typeName": "Text",
            "name": "Text",
            "childIDs": [],
            "text": "Close",
            "font": {
                "__typeName": "DynamicFont",
                "textStyle": "body",
                "isDynamic": true,
                "emphases": []
            },
            "textColor": {
                "systemName": "label",
                "default": {
                    "red": 0,
                    "green": 0,
                    "blue": 0,
                    "alpha": 1
                },
                "darkMode": {
                    "red": 1,
                    "green": 1,
                    "blue": 1,
                    "alpha": 1
                }
            },
            "textAlignment": "leading",
            "offset": [
                0,
                150
            ],
            "action": {
                "__typeName": "CloseAction"
            }
        },
        {
            "id": "675B4C03-3142-4219-B2F7-D224D08BFA1C",
            "__typeName": "Text",
            "name": " Navigate To Screen",
            "childIDs": [],
            "text": "Navigate To Screen C1",
            "font": {
                "__typeName": "DynamicFont",
                "textStyle": "body",
                "isDynamic": true,
                "emphases": []
            },
            "textColor": {
                "systemName": "label",
                "default": {
                    "red": 0,
                    "green": 0,
                    "blue": 0,
                    "alpha": 1
                },
                "darkMode": {
                    "red": 1,
                    "green": 1,
                    "blue": 1,
                    "alpha": 1
                }
            },
            "textAlignment": "leading",
            "action": {
                "__typeName": "PerformSegueAction",
                "screenID": "C81EC2A1-ADD3-4414-917D-97029D96F6C7",
                "segueStyle": "push"
            }
        },
        {
            "id": "C81EC2A1-ADD3-4414-917D-97029D96F6C7",
            "__typeName": "Screen",
            "childIDs": [
                "B36BD7C4-E982-415A-94DC-2108510DB54E",
                "5EC92A6F-756D-4837-AFEF-BD301AD1077C"
            ],
            "name": "Screen C1",
            "backButtonStyle": {
                "__typeName": "DefaultBackButtonStyle",
                "title": "Screen"
            },
            "backgroundColor": {
                "systemName": "systemBackground",
                "default": {
                    "red": 1,
                    "green": 1,
                    "blue": 1,
                    "alpha": 1
                },
                "darkMode": {
                    "red": 0,
                    "green": 0,
                    "blue": 0,
                    "alpha": 1
                }
            },
            "statusBarStyle": "default"
        },
        {
            "id": "B36BD7C4-E982-415A-94DC-2108510DB54E",
            "__typeName": "Text",
            "name": " Navigate To Screen",
            "childIDs": [],
            "text": "Navigate To Screen C2",
            "font": {
                "__typeName": "DynamicFont",
                "textStyle": "body",
                "isDynamic": true,
                "emphases": []
            },
            "textColor": {
                "systemName": "label",
                "default": {
                    "red": 0,
                    "green": 0,
                    "blue": 0,
                    "alpha": 1
                },
                "darkMode": {
                    "red": 1,
                    "green": 1,
                    "blue": 1,
                    "alpha": 1
                }
            },
            "textAlignment": "leading"
        },
        {
            "id": "5EC92A6F-756D-4837-AFEF-BD301AD1077C",
            "__typeName": "Text",
            "name": "Text",
            "childIDs": [],
            "text": "Close",
            "font": {
                "__typeName": "DynamicFont",
                "textStyle": "body",
                "isDynamic": true,
                "emphases": []
            },
            "textColor": {
                "systemName": "label",
                "default": {
                    "red": 0,
                    "green": 0,
                    "blue": 0,
                    "alpha": 1
                },
                "darkMode": {
                    "red": 1,
                    "green": 1,
                    "blue": 1,
                    "alpha": 1
                }
            },
            "textAlignment": "leading",
            "offset": [
                0,
                150
            ],
            "action": {
                "__typeName": "CloseAction"
            }
        },
        {
            "id": "7A9276AB-7929-4953-972A-D98B69DC8A5D",
            "__typeName": "Screen",
            "childIDs": [
                "0C7DC340-A92B-4483-9437-657A35AF3805",
                "28DA12F5-495F-4D1E-972D-A0FB25A0E4C9"
            ],
            "name": "Screen",
            "backButtonStyle": {
                "__typeName": "DefaultBackButtonStyle",
                "title": "Screen"
            },
            "backgroundColor": {
                "systemName": "systemBackground",
                "default": {
                    "red": 1,
                    "green": 1,
                    "blue": 1,
                    "alpha": 1
                },
                "darkMode": {
                    "red": 0,
                    "green": 0,
                    "blue": 0,
                    "alpha": 1
                }
            },
            "statusBarStyle": "default"
        },
        {
            "id": "0C7DC340-A92B-4483-9437-657A35AF3805",
            "__typeName": "Text",
            "name": "Text",
            "childIDs": [],
            "text": "Close",
            "font": {
                "__typeName": "DynamicFont",
                "textStyle": "body",
                "isDynamic": true,
                "emphases": []
            },
            "textColor": {
                "systemName": "label",
                "default": {
                    "red": 0,
                    "green": 0,
                    "blue": 0,
                    "alpha": 1
                },
                "darkMode": {
                    "red": 1,
                    "green": 1,
                    "blue": 1,
                    "alpha": 1
                }
            },
            "textAlignment": "leading",
            "offset": [
                0,
                150
            ],
            "action": {
                "__typeName": "CloseAction"
            }
        },
        {
            "id": "28DA12F5-495F-4D1E-972D-A0FB25A0E4C9",
            "__typeName": "Text",
            "name": " Do It",
            "childIDs": [],
            "text": "Do It Again...Kinda?",
            "font": {
                "__typeName": "DynamicFont",
                "textStyle": "body",
                "isDynamic": true,
                "emphases": []
            },
            "textColor": {
                "systemName": "label",
                "default": {
                    "red": 0,
                    "green": 0,
                    "blue": 0,
                    "alpha": 1
                },
                "darkMode": {
                    "red": 1,
                    "green": 1,
                    "blue": 1,
                    "alpha": 1
                }
            },
            "textAlignment": "leading",
            "action": {
                "__typeName": "PerformSegueAction",
                "screenID": "501241B2-2C40-489A-B6AC-0009FA7F5679",
                "segueStyle": "push"
            }
        }
    ],
    "screenIDs": [
        "8B4A30F1-368A-4026-AD5D-B1F04BE1E7BF",
        "501241B2-2C40-489A-B6AC-0009FA7F5679",
        "764E30E8-E1E1-4DE4-87F1-E281BAE7B8DF",
        "74E163E8-4D07-4570-A1A4-A580FAE48EB0",
        "3FA72273-994B-4AEB-AD3C-E3966FCC43C7",
        "464CE783-4046-4267-971F-C9EB2889C88A",
        "3952BE18-892C-4EAD-9F16-BEA7D54FA22E",
        "C81EC2A1-ADD3-4414-917D-97029D96F6C7",
        "7A9276AB-7929-4953-972A-D98B69DC8A5D"
    ],
    "initialScreenID": "8B4A30F1-368A-4026-AD5D-B1F04BE1E7BF",
    "fonts": [],
    "localization": {}
}""".trimMargin()


    val data_source_experience = """{
    "id": "256",
    "name": "DataSources Test",
    "version": 1,
    "revisionID": "248",
    "nodes": [
        {
            "id": "086DE36F-EEE4-482A-BD45-60124EC03CC7",
            "__typeName": "Screen",
            "childIDs": [
                "0B2BA390-9C0A-4128-A34C-E132B9DB585C"
            ],
            "name": "Screen",
            "backButtonStyle": {
                "__typeName": "DefaultBackButtonStyle",
                "title": "Screen"
            },
            "backgroundColor": {
                "systemName": "systemBackground",
                "default": {
                    "red": 1,
                    "green": 1,
                    "blue": 1,
                    "alpha": 1
                },
                "darkMode": {
                    "red": 0,
                    "green": 0,
                    "blue": 0,
                    "alpha": 1
                }
            },
            "statusBarStyle": "default",
            "androidStatusBarStyle": "default",
            "statusBarBackgroundColor": {
                "default": {
                    "red": 0.21568627450980393,
                    "alpha": 1,
                    "blue": 0.7019607843137254,
                    "green": 0
                }
            }
        },
        {
            "id": "0B2BA390-9C0A-4128-A34C-E132B9DB585C",
            "__typeName": "ScrollContainer",
            "childIDs": [
                "B8AA4599-DDC5-4AA7-BA7B-11A52603A71C"
            ],
            "name": "Scroll Container",
            "axis": "vertical",
            "disableScrollBar": false
        },
        {
            "id": "B8AA4599-DDC5-4AA7-BA7B-11A52603A71C",
            "__typeName": "DataSource",
            "childIDs": [
                "5FC3CE16-6BF6-4214-92C7-5F6FBB067409"
            ],
            "name": "Dummy Users",
            "headers": [
                {
                    "key": "Content-Type",
                    "value": "application/json"
                },
                {
                    "key": "app-id",
                    "value": "609561d4e8fed0600a0a26b8"
                }
            ],
            "httpMethod": "GET",
            "url": "https://dummyapi.io/data/api/user?limit=10"
        },
        {
            "id": "5FC3CE16-6BF6-4214-92C7-5F6FBB067409",
            "__typeName": "Collection",
            "childIDs": [
                "53F6CBB6-050C-492C-A854-D01C9F5A1323"
            ],
            "name": "Collection",
            "dataKey": "data",
            "filters": [],
            "sortDescriptors": []
        },
        {
            "id": "53F6CBB6-050C-492C-A854-D01C9F5A1323",
            "__typeName": "Text",
            "childIDs": [],
            "text": "Hello, {{data.firstName}}",
            "font": {
                "__typeName": "DynamicFont",
                "textStyle": "body",
                "isDynamic": true,
                "emphases": []
            },
            "textColor": {
                "systemName": "label",
                "default": {
                    "red": 0,
                    "green": 0,
                    "blue": 0,
                    "alpha": 1
                },
                "darkMode": {
                    "red": 1,
                    "green": 1,
                    "blue": 1,
                    "alpha": 1
                }
            },
            "textAlignment": "leading"
        }
    ],
    "screenIDs": [
        "086DE36F-EEE4-482A-BD45-60124EC03CC7"
    ],
    "initialScreenID": "086DE36F-EEE4-482A-BD45-60124EC03CC7",
    "fonts": [],
    "localization": {
        "fr": {
            "Hello, {{data.firstName}}": "Bonjour, {{data.firstName}}"
        }
    },
    "appearance": "auto"
}""".trimMargin()

    val data_source_experience_single_screen = """{
    "id": "265",
    "name": "DataSources Test",
    "version": 1,
    "revisionID": "257",
    "nodes": [
        {
            "id": "DF5409B8-01F6-4803-8063-09B7013D73AA",
            "__typeName": "Screen",
            "childIDs": [
                "1758EDF6-4C8F-4039-9C3A-0612AE0DE096"
            ],
            "name": "Screen",
            "backButtonStyle": {
                "__typeName": "DefaultBackButtonStyle",
                "title": "Screen 2"
            },
            "backgroundColor": {
                "systemName": "systemBackground",
                "default": {
                    "red": 1,
                    "green": 1,
                    "blue": 1,
                    "alpha": 1
                },
                "darkMode": {
                    "red": 0,
                    "green": 0,
                    "blue": 0,
                    "alpha": 1
                }
            },
            "statusBarStyle": "default",
            "androidStatusBarStyle": "default",
            "statusBarBackgroundColor": {
                "default": {
                    "red": 0.21568627450980393,
                    "alpha": 1,
                    "blue": 0.7019607843137254,
                    "green": 0
                }
            }
        },
        {
            "id": "1758EDF6-4C8F-4039-9C3A-0612AE0DE096",
            "__typeName": "ScrollContainer",
            "childIDs": [
                "62CC412B-179B-4112-B96F-4E7B2BCF73BB"
            ],
            "name": "Scroll Container",
            "axis": "vertical",
            "disableScrollBar": false
        },
        {
            "id": "62CC412B-179B-4112-B96F-4E7B2BCF73BB",
            "__typeName": "DataSource",
            "childIDs": [
                "210F73CC-672B-4543-AF12-7BCF766626E8",
                "C64FF06C-69B2-44DB-A369-768ECC9C652A",
                "8FFD5E5A-A1FF-420D-B585-F4074D0158F3"
            ],
            "name": "Dummy Users",
            "headers": [
                {
                    "key": "Content-Type",
                    "value": "application/json"
                },
                {
                    "key": "app-id",
                    "value": "609561d4e8fed0600a0a26b8"
                }
            ],
            "httpMethod": "GET",
            "url": "https://dummyapi.io/data/api/user?limit=10"
        },
        {
            "id": "210F73CC-672B-4543-AF12-7BCF766626E8",
            "__typeName": "Text",
            "childIDs": [],
            "text": "USER COUNT: {{data.limit}}",
            "font": {
                "__typeName": "DynamicFont",
                "textStyle": "body",
                "isDynamic": true,
                "emphases": []
            },
            "textColor": {
                "systemName": "label",
                "default": {
                    "red": 0,
                    "green": 0,
                    "blue": 0,
                    "alpha": 1
                },
                "darkMode": {
                    "red": 1,
                    "green": 1,
                    "blue": 1,
                    "alpha": 1
                }
            },
            "textAlignment": "leading"
        },
        {
            "id": "C64FF06C-69B2-44DB-A369-768ECC9C652A",
            "__typeName": "Collection",
            "childIDs": [
                "8D9A9AB3-800D-4891-93D9-7E9CD52963EC"
            ],
            "name": "Collection",
            "dataKey": "data",
            "filters": [],
            "sortDescriptors": []
        },
        {
            "id": "8D9A9AB3-800D-4891-93D9-7E9CD52963EC",
            "__typeName": "VStack",
            "childIDs": [
                "1A1531E8-1276-44D9-86B7-D5C441843596",
                "F55CA332-6753-4E1F-AC07-208116BFB2B1"
            ],
            "alignment": "leading",
            "spacing": 1
        },
        {
            "id": "1A1531E8-1276-44D9-86B7-D5C441843596",
            "__typeName": "HStack",
            "childIDs": [
                "F1CC884C-F5AD-4124-88F5-E2421CC0C2C8",
                "6B5BCE96-8B93-44D2-9DFE-A5BA94987D80",
                "55C95A98-CDBA-4C4E-917C-58D610F976F0",
                "18D5D662-3E39-43D2-86C0-D79B887D149D"
            ],
            "name": "HStack",
            "padding": {
                "trailing": 0,
                "top": 0,
                "leading": 20,
                "bottom": 0
            },
            "alignment": "center",
            "spacing": 0
        },
        {
            "id": "F1CC884C-F5AD-4124-88F5-E2421CC0C2C8",
            "__typeName": "Text",
            "childIDs": [],
            "name": " Hello,",
            "text": "Hello, ",
            "font": {
                "__typeName": "DynamicFont",
                "textStyle": "body",
                "isDynamic": true,
                "emphases": []
            },
            "textColor": {
                "systemName": "label",
                "default": {
                    "red": 0,
                    "green": 0,
                    "blue": 0,
                    "alpha": 1
                },
                "darkMode": {
                    "red": 1,
                    "green": 1,
                    "blue": 1,
                    "alpha": 1
                }
            },
            "textAlignment": "leading"
        },
        {
            "id": "6B5BCE96-8B93-44D2-9DFE-A5BA94987D80",
            "__typeName": "Text",
            "childIDs": [],
            "name": "First Name",
            "text": "{{data.firstName}} ",
            "font": {
                "__typeName": "DynamicFont",
                "textStyle": "body",
                "isDynamic": true,
                "emphases": []
            },
            "textColor": {
                "systemName": "label",
                "default": {
                    "red": 0,
                    "green": 0,
                    "blue": 0,
                    "alpha": 1
                },
                "darkMode": {
                    "red": 1,
                    "green": 1,
                    "blue": 1,
                    "alpha": 1
                }
            },
            "textAlignment": "leading"
        },
        {
            "id": "55C95A98-CDBA-4C4E-917C-58D610F976F0",
            "__typeName": "Text",
            "childIDs": [],
            "name": "Last Name",
            "text": "{{data.lastName}}",
            "font": {
                "__typeName": "DynamicFont",
                "textStyle": "body",
                "isDynamic": true,
                "emphases": []
            },
            "textColor": {
                "systemName": "label",
                "default": {
                    "red": 0,
                    "green": 0,
                    "blue": 0,
                    "alpha": 1
                },
                "darkMode": {
                    "red": 1,
                    "green": 1,
                    "blue": 1,
                    "alpha": 1
                }
            },
            "textAlignment": "leading"
        },
        {
            "id": "18D5D662-3E39-43D2-86C0-D79B887D149D",
            "__typeName": "Spacer",
            "childIDs": []
        },
        {
            "id": "F55CA332-6753-4E1F-AC07-208116BFB2B1",
            "__typeName": "Spacer",
            "childIDs": [],
            "frame": {
                "alignment": "center",
                "height": 0
            }
        },
        {
            "id": "8FFD5E5A-A1FF-420D-B585-F4074D0158F3",
            "__typeName": "DataSource",
            "childIDs": [
                "48C41C65-623F-4B88-9374-F2A17312A7D0",
                "A8312ABE-B99F-40B6-ACD2-453BEC598408"
            ],
            "name": "Data Source",
            "headers": [],
            "httpMethod": "GET",
            "url": "https://reqres.in/api/users"
        },
        {
            "id": "48C41C65-623F-4B88-9374-F2A17312A7D0",
            "__typeName": "Rectangle",
            "childIDs": [],
            "name": "Rectangle",
            "frame": {
                "alignment": "center",
                "height": 4
            },
            "cornerRadius": 0,
            "fill": {
                "__typeName": "FlatFill",
                "color": {
                    "systemName": "systemFill",
                    "default": {
                        "red": 0.47058823529411764,
                        "green": 0.47058823529411764,
                        "blue": 0.5019607843137255,
                        "alpha": 0.2
                    },
                    "darkMode": {
                        "red": 0.47058823529411764,
                        "green": 0.47058823529411764,
                        "blue": 0.5019607843137255,
                        "alpha": 0.36
                    }
                }
            }
        },
        {
            "id": "A8312ABE-B99F-40B6-ACD2-453BEC598408",
            "__typeName": "VStack",
            "childIDs": [
                "2794D179-4D10-4014-B3C8-2C91C8BFB70F",
                "7ADE52D3-2481-407A-B00E-611A9CF7C0A3"
            ],
            "alignment": "center",
            "spacing": 8
        },
        {
            "id": "2794D179-4D10-4014-B3C8-2C91C8BFB70F",
            "__typeName": "Collection",
            "childIDs": [
                "A90B03B5-4EEF-455A-9B71-EA7F44768276"
            ],
            "name": "Collection",
            "dataKey": "data",
            "filters": [],
            "sortDescriptors": []
        },
        {
            "id": "A90B03B5-4EEF-455A-9B71-EA7F44768276",
            "__typeName": "Text",
            "childIDs": [],
            "name": " Hello,",
            "text": "Hello, {{data.last_name}}",
            "font": {
                "__typeName": "DynamicFont",
                "textStyle": "body",
                "isDynamic": true,
                "emphases": []
            },
            "textColor": {
                "systemName": "systemPink",
                "default": {
                    "red": 1,
                    "green": 0.17647058823529413,
                    "blue": 0.3333333333333333,
                    "alpha": 1
                },
                "darkMode": {
                    "red": 1,
                    "green": 0.21568627450980393,
                    "blue": 0.37254901960784315,
                    "alpha": 1
                },
                "highContrast": {
                    "red": 0.8274509803921568,
                    "green": 0.058823529411764705,
                    "blue": 0.27058823529411763,
                    "alpha": 1
                },
                "darkModeHighContrast": {
                    "red": 1,
                    "green": 0.39215686274509803,
                    "blue": 0.5098039215686274,
                    "alpha": 1
                }
            },
            "textAlignment": "leading"
        },
        {
            "id": "7ADE52D3-2481-407A-B00E-611A9CF7C0A3",
            "__typeName": "Collection",
            "childIDs": [
                "BF0AC99B-31C0-4794-BE63-D09E30ADCAEA"
            ],
            "dataKey": "data",
            "filters": [],
            "sortDescriptors": []
        },
        {
            "id": "BF0AC99B-31C0-4794-BE63-D09E30ADCAEA",
            "__typeName": "Text",
            "childIDs": [],
            "name": " Hello,",
            "text": "Hello, {{data.last_name}}",
            "font": {
                "__typeName": "DynamicFont",
                "textStyle": "body",
                "isDynamic": true,
                "emphases": []
            },
            "textColor": {
                "systemName": "systemBlue",
                "default": {
                    "red": 0,
                    "green": 0.47843137254901963,
                    "blue": 1,
                    "alpha": 1
                },
                "darkMode": {
                    "red": 0.0392156862745098,
                    "green": 0.5176470588235295,
                    "blue": 1,
                    "alpha": 1
                },
                "highContrast": {
                    "red": 0,
                    "green": 0.25098039215686274,
                    "blue": 0.8666666666666667,
                    "alpha": 1
                },
                "darkModeHighContrast": {
                    "red": 0.25098039215686274,
                    "green": 0.611764705882353,
                    "blue": 1,
                    "alpha": 1
                }
            },
            "textAlignment": "leading"
        }
    ],
    "screenIDs": [
        "DF5409B8-01F6-4803-8063-09B7013D73AA"
    ],
    "initialScreenID": "DF5409B8-01F6-4803-8063-09B7013D73AA",
    "fonts": [],
    "localization": {
        "fr": {
            "Hello, {{data.firstName}}": "Bonjour, {{data.firstName}}"
        }
    },
    "appearance": "auto"
}""".trimMargin()

    val dummy_api_response: String = """{
    "data": [
        {
            "id": "0F8JIqi4zwvb77FGz6Wt",
            "lastName": "Fiedler",
            "firstName": "Heinz-Georg",
            "email": "heinz-georg.fiedler@example.com",
            "title": "mr",
            "picture": "https://randomuser.me/api/portraits/men/81.jpg"
        },
        {
            "id": "0P6E1d4nr0L1ntW8cjGU",
            "picture": "https://randomuser.me/api/portraits/women/74.jpg",
            "lastName": "Hughes",
            "email": "katie.hughes@example.com",
            "title": "miss",
            "firstName": "Katie"
        },
        {
            "id": "1Lkk06cOUCkiAsUXFLMN",
            "title": "mr",
            "lastName": "Aasland",
            "firstName": "Vetle",
            "picture": "https://randomuser.me/api/portraits/men/97.jpg",
            "email": "vetle.aasland@example.com"
        },
        {
            "id": "1OuR3CWOEsfISTpFxsG7",
            "picture": "https://randomuser.me/api/portraits/men/66.jpg",
            "lastName": "Vasquez",
            "email": "dylan.vasquez@example.com",
            "title": "mr",
            "firstName": "Dylan"
        },
        {
            "id": "1pRsh5nXDIH3pjEOZ17A",
            "lastName": "Vicente",
            "title": "miss",
            "firstName": "Margarita",
            "email": "margarita.vicente@example.com",
            "picture": "https://randomuser.me/api/portraits/women/5.jpg"
        },
        {
            "id": "3JAf8R85oIlxXd58Piqk",
            "email": "joey.oliver@example.com",
            "title": "mr",
            "firstName": "Joey",
            "lastName": "Oliver",
            "picture": "https://randomuser.me/api/portraits/men/61.jpg"
        },
        {
            "id": "5aZRSdkcBOM6j3lkWEoP",
            "picture": "https://randomuser.me/api/portraits/women/50.jpg",
            "email": "lilja.lampinen@example.com",
            "lastName": "Lampinen",
            "firstName": "Lilja",
            "title": "ms"
        },
        {
            "id": "5tVxgsqPCjv2Ul5Rc7gw",
            "email": "abigail.liu@example.com",
            "lastName": "Liu",
            "title": "miss",
            "picture": "https://randomuser.me/api/portraits/women/83.jpg",
            "firstName": "Abigail"
        },
        {
            "id": "6wy6UNkZueJfIUfq88d5",
            "picture": "https://randomuser.me/api/portraits/women/32.jpg",
            "firstName": "Melanie",
            "email": "melanie.pilz@example.com",
            "title": "miss",
            "lastName": "Pilz"
        },
        {
            "id": "7DbXNPWlNDR4QYVvFZjr",
            "email": "evan.carlson@example.com",
            "firstName": "Evan",
            "picture": "https://randomuser.me/api/portraits/men/80.jpg",
            "lastName": "Carlson",
            "title": "mr"
        }
    ],
    "total": 100,
    "page": 0,
    "limit": 10,
    "offset": 0
}""".trimMargin()

    val reqres_api_response: String = """{
  "page": 1,
  "per_page": 6,
  "total": 12,
  "total_pages": 2,
  "data": [
    {
      "id": 1,
      "email": "george.bluth@reqres.in",
      "first_name": "George",
      "last_name": "Bluth",
      "avatar": "https://reqres.in/img/faces/1-image.jpg"
    },
    {
      "id": 2,
      "email": "janet.weaver@reqres.in",
      "first_name": "Janet",
      "last_name": "Weaver",
      "avatar": "https://reqres.in/img/faces/2-image.jpg"
    },
    {
      "id": 3,
      "email": "emma.wong@reqres.in",
      "first_name": "Emma",
      "last_name": "Wong",
      "avatar": "https://reqres.in/img/faces/3-image.jpg"
    },
    {
      "id": 4,
      "email": "eve.holt@reqres.in",
      "first_name": "Eve",
      "last_name": "Holt",
      "avatar": "https://reqres.in/img/faces/4-image.jpg"
    },
    {
      "id": 5,
      "email": "charles.morris@reqres.in",
      "first_name": "Charles",
      "last_name": "Morris",
      "avatar": "https://reqres.in/img/faces/5-image.jpg"
    },
    {
      "id": 6,
      "email": "tracey.ramos@reqres.in",
      "first_name": "Tracey",
      "last_name": "Ramos",
      "avatar": "https://reqres.in/img/faces/6-image.jpg"
    }
  ],
  "support": {
    "url": "https://reqres.in/#support-heading",
    "text": "To keep ReqRes free, contributions towards server costs are appreciated!"
  }
}""".trimMargin()

    val user_data_experience: String = """{
    "id": "346",
    "name": "User Data Test",
    "version": 1,
    "revisionID": "337",
    "nodes": [
        {
            "id": "7AEF5C98-4A6B-41B3-ADB9-358DC13D441F",
            "__typeName": "Screen",
            "childIDs": [
                "9ED436F2-BB9F-4AEE-84D0-9D2E40C56AC9"
            ],
            "name": "Screen",
            "backButtonStyle": {
                "__typeName": "DefaultBackButtonStyle",
                "title": "Screen"
            },
            "backgroundColor": {
                "systemName": "systemBackground",
                "default": {
                    "red": 1,
                    "green": 1,
                    "blue": 1,
                    "alpha": 1
                },
                "darkMode": {
                    "red": 0,
                    "green": 0,
                    "blue": 0,
                    "alpha": 1
                }
            },
            "statusBarStyle": "default",
            "androidStatusBarStyle": "default",
            "statusBarBackgroundColor": {
                "default": {
                    "red": 0.21568627450980393,
                    "alpha": 1,
                    "blue": 0.7019607843137254,
                    "green": 0
                }
            }
        },
        {
            "id": "9ED436F2-BB9F-4AEE-84D0-9D2E40C56AC9",
            "__typeName": "Text",
            "childIDs": [],
            "text": "Hello, {{  user.firstName  }} {{ user.lastName }}",
            "font": {
                "__typeName": "DynamicFont",
                "textStyle": "body",
                "isDynamic": true,
                "emphases": []
            },
            "textColor": {
                "systemName": "label",
                "default": {
                    "red": 0,
                    "green": 0,
                    "blue": 0,
                    "alpha": 1
                },
                "darkMode": {
                    "red": 1,
                    "green": 1,
                    "blue": 1,
                    "alpha": 1
                }
            },
            "textAlignment": "leading"
        }
    ],
    "screenIDs": [
        "7AEF5C98-4A6B-41B3-ADB9-358DC13D441F"
    ],
    "initialScreenID": "7AEF5C98-4A6B-41B3-ADB9-358DC13D441F",
    "fonts": [],
    "localization": {},
    "appearance": "auto",
    "prefetchImages": "none"
}""".trimMargin()

}