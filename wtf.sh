echo "implementation time: 2h"
echo "number of lines:"
find . ! -path ./node_modules?* ! -path ./dist?* ! -path ./package-lock.json ! -path "./.git?*" -type f -exec cat {} + | wc -l
echo "///////////////////////////"
echo "///////////////////////////"
echo "///////////////////////////"
echo "bare:"
ab -c 10 -n 1000 -u teststatus.json -T application/x-www-form-urlencoded http://xocore.hopto.org:32780/status
echo "///////////////////////////"
echo "///////////////////////////"
echo "///////////////////////////"
echo "bare2:"
ab -c 100 -n 1000 -u teststatus.json -T application/x-www-form-urlencoded http://xocore.hopto.org:32780/status
echo "///////////////////////////"
echo "///////////////////////////"
echo "///////////////////////////"
echo "status test:"
ab -c 10 -n 1000 -u teststatus.json -T application/x-www-form-urlencoded http://localhost:3000/FreshDesk/34677/status
echo "///////////////////////////"
echo "///////////////////////////"
echo "///////////////////////////"
echo "status test2:"
ab -c 100 -n 1000 -u teststatus.json -T application/x-www-form-urlencoded http://localhost:3000/FreshDesk/34677/status
echo "///////////////////////////"
echo "///////////////////////////"
echo "///////////////////////////"
echo "message test:"
ab -c 10 -n 1000 -p testmessage.json -T application/x-www-form-urlencoded http://localhost:3000/FreshDesk/34677/message
echo "///////////////////////////"
echo "///////////////////////////"
echo "///////////////////////////"
echo "message test2:"
ab -c 100 -n 1000 -p testmessage.json -T application/x-www-form-urlencoded http://localhost:3000/FreshDesk/34677/message
