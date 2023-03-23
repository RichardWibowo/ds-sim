
## Overview
ds-sim is a discrete-event simulator that has been developed primarily for leveraging scheduling algorithm design. It adopts a minimalist design explicitly taking into account modularity in that it uses the client-server model. The client-side simulator acts as a job scheduler while the server-side simulator simulates everything else including users (job submissions) and servers (job execution).

---
## How to run a simulation
1. run server `$ ds-server [OPTION]...`
2. run client `$ ds-client [-a algorithm] [OPTION]...`

## Usage
`$ ds-server -c ds-config01.xml -v brief`

`$ ds-client -a bf`

---
micro 0 inactive -1 2 4000 16000 0 0
micro 1 inactive -1 2 4000 16000 0 0
small 0 inactive -1 4 16000 64000 0 0
small 1 inactive -1 4 16000 64000 0 0
medium 0 inactive -1 8 32000 128000 0 0
medium 1 inactive -1 8 32000 128000 0 0
medium 2 inactive -1 8 32000 128000 0 0
.
.
